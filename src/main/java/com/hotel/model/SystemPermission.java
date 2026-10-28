package com.hotel.model;

import javax.persistence.*;

@Entity
@Table(name = "he_thong_quyen")
public class SystemPermission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") private Integer id;
    @Column(name = "ma_quyen", nullable = false, unique = true, length = 60) private String code;
    @Column(name = "ten_hien_thi", nullable = false, length = 120) private String displayName;
    @Column(name = "mo_ta", length = 255) private String description;
    @Column(name = "mau_url", nullable = false, length = 120) private String urlPattern;
    public Integer getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getUrlPattern() { return urlPattern; }
    public void setUrlPattern(String urlPattern) { this.urlPattern = urlPattern; }
}
