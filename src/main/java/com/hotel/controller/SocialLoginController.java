package com.hotel.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hotel.model.TaiKhoan;
import com.hotel.repository.TaiKhoanRepository;
import com.hotel.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.UUID;

@Controller
public class SocialLoginController {
    @Value("${app.oauth.google-url:}")
    private String googleUrl;
    @Value("${app.oauth.facebook-url:}")
    private String facebookUrl;
    @Value("${app.oauth.google-client-id:}")
    private String googleClientId;
    @Value("${app.oauth.google-client-secret:}")
    private String googleClientSecret;
    @Value("${app.oauth.facebook-client-id:}")
    private String facebookClientId;
    @Value("${app.oauth.facebook-client-secret:}")
    private String facebookClientSecret;
    @Value("${app.oauth.redirect-base-url:http://localhost:8080}")
    private String redirectBaseUrl;

    private final TaiKhoanRepository taiKhoanRepository;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public SocialLoginController(TaiKhoanRepository taiKhoanRepository) {
        this.taiKhoanRepository = taiKhoanRepository;
    }

    @GetMapping("/login/{provider}")
    public void login(@PathVariable String provider, HttpServletResponse response,
                      HttpServletRequest request) throws IOException {
        String normalizedProvider = provider == null ? "" : provider.trim().toLowerCase();
        String target = "google".equals(normalizedProvider) ? googleUrl
                : "facebook".equals(normalizedProvider) ? facebookUrl : null;

        if (target == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (target.isBlank()) {
            target = authorizationUrl(normalizedProvider);
        }
        if (target.isBlank()) {
            String contextPath = request.getContextPath();
            String message = "Đăng nhập " + normalizedProvider
                    + " chưa được cấu hình. Vui lòng thiết lập biến môi trường "
                    + normalizedProvider.toUpperCase() + "_OAUTH_URL.";
            response.sendRedirect(contextPath + "/login?oauthError="
                    + java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8));
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("oauthProvider", normalizedProvider);
        response.sendRedirect(target);
    }

    private String authorizationUrl(String provider) {
        String clientId = "google".equals(provider) ? googleClientId : facebookClientId;
        if (clientId == null || clientId.isBlank()) {
            return "";
        }
        String redirectUri = redirectBaseUrl + "/oauth2/callback/" + provider;
        if ("google".equals(provider)) {
            return "https://accounts.google.com/o/oauth2/v2/auth?response_type=code"
                    + "&client_id=" + encode(clientId)
                    + "&redirect_uri=" + encode(redirectUri)
                    + "&scope=" + encode("openid email profile")
                    + "&access_type=offline";
        }
        return "https://www.facebook.com/v19.0/dialog/oauth?response_type=code"
                + "&client_id=" + encode(clientId)
                + "&redirect_uri=" + encode(redirectUri)
                + "&scope=" + encode("email public_profile");
    }

    @GetMapping("/oauth2/callback/{provider}")
    public void callback(@PathVariable String provider,
                         @RequestParam(required = false) String code,
                         @RequestParam(required = false) String error,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        String normalizedProvider = provider == null ? "" : provider.trim().toLowerCase();
        if (!"google".equals(normalizedProvider) && !"facebook".equals(normalizedProvider)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (error != null || code == null || code.isBlank()) {
            redirectWithError(request, response, "Đăng nhập " + normalizedProvider + " đã bị hủy hoặc thất bại.");
            return;
        }

        try {
            JsonObject profile = fetchProfile(normalizedProvider, code);
            String providerId = value(profile, "id", "sub");
            String email = value(profile, "email");
            String name = value(profile, "name", "given_name");
            if (providerId == null || email == null || email.isBlank()) {
                throw new IllegalStateException("Nhà cung cấp không trả về email hoặc mã người dùng.");
            }

            TaiKhoan account = taiKhoanRepository
                    .findByProviderAndProviderId(normalizedProvider, providerId)
                    .orElseGet(() -> taiKhoanRepository.findByEmailIgnoreCase(email).orElse(null));
            if (account != null && !"CUSTOMER".equals(account.getVaiTro())) {
                throw new IllegalStateException("Tài khoản local không thể đăng nhập bằng OAuth.");
            }
            if (account == null) {
                account = new TaiKhoan();
                account.setTenDangNhap(uniqueUsername(normalizedProvider, providerId));
                account.setMatKhau(PasswordUtil.hash(UUID.randomUUID().toString()));
                account.setVaiTro("CUSTOMER");
                account.setTrangThai(1);
                account.setNgayTao(new Timestamp(System.currentTimeMillis()));
            }
            account.setHoTen(name == null || name.isBlank() ? email : name);
            account.setEmail(email);
            account.setProvider(normalizedProvider);
            account.setProviderId(providerId);
            account.setAnhDaiDien(safeAvatarUrl(profileImageUrl(profile)));
            account = taiKhoanRepository.save(account);

            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            request.getSession(true).setAttribute("currentUser", account);
            response.sendRedirect(request.getContextPath() + "/customer/profile");
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            redirectWithError(request, response, "Không thể hoàn tất đăng nhập mạng xã hội.");
        } catch (Exception ex) {
            redirectWithError(request, response, "Đăng nhập mạng xã hội thất bại: " + ex.getMessage());
        }
    }

    private JsonObject fetchProfile(String provider, String code)
            throws IOException, InterruptedException {
        String redirectUri = redirectBaseUrl + "/oauth2/callback/" + provider;
        String clientId = "google".equals(provider) ? googleClientId : facebookClientId;
        String clientSecret = "google".equals(provider) ? googleClientSecret : facebookClientSecret;
        String tokenEndpoint = "google".equals(provider)
                ? "https://oauth2.googleapis.com/token" : "https://graph.facebook.com/v19.0/oauth/access_token";
        String tokenBody = form("client_id", clientId, "client_secret", clientSecret,
                "code", code, "redirect_uri", redirectUri,
                "grant_type", "authorization_code");
        String tokenResponse = send(tokenEndpoint, tokenBody, "application/x-www-form-urlencoded");
        String accessToken = value(JsonParser.parseString(tokenResponse).getAsJsonObject(), "access_token");
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException("Không nhận được access token.");
        }
        String profileEndpoint = "google".equals(provider)
                ? "https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + encode(accessToken)
                : "https://graph.facebook.com/me?fields=id,name,email,picture&access_token=" + encode(accessToken);
        return JsonParser.parseString(send(profileEndpoint, null, null)).getAsJsonObject();
    }

    private String send(String url, String body, String contentType)
            throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url));
        if (body == null) {
            builder.GET();
        } else {
            builder.header("Content-Type", contentType).POST(HttpRequest.BodyPublishers.ofString(body));
        }
        HttpResponse<String> result = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (result.statusCode() < 200 || result.statusCode() >= 300) {
            throw new IOException("OAuth provider trả về HTTP " + result.statusCode() + ".");
        }
        return result.body();
    }

    private String uniqueUsername(String provider, String providerId) {
        String base = provider + "_" + providerId;
        return taiKhoanRepository.findByTenDangNhapAndTrangThai(base, 1).isEmpty()
                ? base : base + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String value(JsonObject object, String... names) {
        for (String name : names) {
            if (object.has(name) && !object.get(name).isJsonNull()) {
                if (object.get(name).isJsonPrimitive()) {
                    return object.get(name).getAsString();
                }
            }
        }
        return null;
    }

    private String profileImageUrl(JsonObject profile) {
        String directUrl = value(profile, "picture_url", "picture");
        if (directUrl != null) {
            return directUrl;
        }
        if (profile.has("picture") && profile.get("picture").isJsonObject()) {
            JsonObject picture = profile.getAsJsonObject("picture");
            if (picture.has("data") && picture.get("data").isJsonObject()) {
                return value(picture.getAsJsonObject("data"), "url");
            }
        }
        return null;
    }

    private String safeAvatarUrl(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.length() <= 255) {
            return avatarUrl;
        }
        return null;
    }

    private String form(String... values) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.length; i += 2) {
            if (result.length() > 0) result.append('&');
            result.append(encode(values[i])).append('=').append(encode(values[i + 1]));
        }
        return result.toString();
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private void redirectWithError(HttpServletRequest request, HttpServletResponse response,
                                   String message) throws IOException {
        response.sendRedirect(request.getContextPath() + "/login?oauthError=" + encode(message));
    }
}
