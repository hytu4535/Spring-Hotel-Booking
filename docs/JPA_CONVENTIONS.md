# Quy ước sử dụng Spring Data JPA

## 1. Repository

- Repository sử dụng Spring Data JPA và kế thừa `JpaRepository`.
- Không viết JDBC hoặc SQL thủ công cho các thao tác CRUD thông thường.
- Chỉ sử dụng truy vấn tùy chỉnh khi phương thức có sẵn của JPA không đáp ứng được yêu cầu.

## 2. Service

- Controller không truy vấn database trực tiếp.
- Service chịu trách nhiệm xử lý nghiệp vụ và gọi Repository để thao tác dữ liệu.
- Các nghiệp vụ liên quan đến nhiều bảng phải được xử lý tập trung trong Service, tránh phân tán logic giữa các Controller.

## 3. Transaction

- Sử dụng `@Transactional` cho các nghiệp vụ cần bảo đảm tính toàn vẹn dữ liệu.
- Những thao tác phải thành công hoặc thất bại cùng nhau cần nằm trong cùng một transaction.
- Ví dụ: lưu đơn đặt phòng và các chi tiết đặt phòng phải thành công hoặc rollback cùng nhau.

## 4. DTO

- Không trả Entity trực tiếp cho frontend.
- Sử dụng DTO để nhận dữ liệu request và trả dữ liệu response.
- DTO chỉ chứa các trường cần thiết cho từng chức năng, tránh để lộ thông tin nhạy cảm và hạn chế lỗi vòng lặp JSON do quan hệ giữa các Entity.
