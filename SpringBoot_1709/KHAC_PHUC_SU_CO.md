# Khắc phục sự cố OneShop

## 🚨 Các lỗi đã được khắc phục

### 1. Lỗi phải đăng nhập 2 lần

**Nguyên nhân:** Session management không được cấu hình đúng cách

**Giải pháp đã áp dụng:**

- ✅ Thêm SessionRegistry vào SecurityConfig
- ✅ Cập nhật session management với proper configuration
- ✅ Thêm CSRF token handling đúng cách

### 2. Lỗi bị văng ra trang login khi lưu sản phẩm

**Nguyên nhân:** CSRF token không được xử lý đúng cách

**Giải pháp đã áp dụng:**

- ✅ Thêm CSRF token vào layout.html
- ✅ Thêm @ModelAttribute("\_csrf") vào tất cả controllers
- ✅ Cập nhật SecurityConfig để xử lý CSRF đúng cách

## 🔧 Các thay đổi chi tiết

### SecurityConfig.java

```java
// Thêm SessionRegistry
@Bean
public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
}

// Cập nhật session management
.sessionManagement(session -> session
    .maximumSessions(1)
    .maxSessionsPreventsLogin(false)
    .sessionRegistry(sessionRegistry())
)
```

### Layout.html

```html
<!-- Thêm CSRF token -->
<meta name="_csrf" th:content="${_csrf.token}" />
<meta name="_csrf_header" th:content="${_csrf.headerName}" />
```

### Controllers

```java
// Thêm vào tất cả controllers
@ModelAttribute("_csrf")
public CsrfToken csrfToken(HttpServletRequest request) {
    return (CsrfToken) request.getAttribute("_csrf");
}
```

## 🚀 Cách test

### 1. Khởi tạo dữ liệu

```
http://localhost:8089/init/data
```

### 2. Đăng nhập (chỉ cần 1 lần)

- Username: `admin`
- Password: `admin123`

### 3. Test chức năng lưu sản phẩm

- Vào trang Products
- Click "Thêm sản phẩm mới"
- Điền thông tin và click "Lưu sản phẩm"
- Sản phẩm sẽ được lưu thành công và không bị văng ra login

## 📝 Ghi chú quan trọng

1. **Session Management**: Đã được cấu hình để tránh lỗi đăng nhập nhiều lần
2. **CSRF Protection**: Đã được xử lý đúng cách cho form submission
3. **Static Resources**: Đã được cho phép truy cập mà không cần đăng nhập
4. **API Endpoints**: CSRF đã được tắt cho API để tránh xung đột

## 🔍 Debug nếu vẫn có lỗi

### Kiểm tra Console Log

- Mở Developer Tools (F12)
- Xem tab Console để kiểm tra lỗi JavaScript
- Xem tab Network để kiểm tra request/response

### Kiểm tra Server Log

- Xem log của Spring Boot để kiểm tra lỗi backend
- Tìm kiếm từ khóa: "CSRF", "Session", "Authentication"

### Kiểm tra Database

- Đảm bảo bảng `users` và `roles` đã có dữ liệu
- Kiểm tra user `admin` có role `ADMIN` không

## ✅ Kết quả mong đợi

Sau khi áp dụng các fix trên:

1. ✅ Đăng nhập chỉ cần 1 lần
2. ✅ Lưu sản phẩm thành công không bị văng ra login
3. ✅ Lưu danh mục thành công không bị văng ra login
4. ✅ Tất cả chức năng CRUD hoạt động bình thường
