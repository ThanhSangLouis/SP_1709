# Hướng dẫn sử dụng OneShop

## 🚀 Cách chạy project

### 1. Khởi tạo dữ liệu

Trước khi sử dụng, hãy truy cập URL sau để khởi tạo dữ liệu mẫu:

```
http://localhost:8089/init/data
```

### 2. Đăng nhập

- Truy cập: `http://localhost:8089/login`
- Username: `admin`
- Password: `admin123`

### 3. Các trang chính

- **Dashboard**: `http://localhost:8089/dashboard`
- **Quản lý danh mục**: `http://localhost:8089/categories`
- **Quản lý sản phẩm**: `http://localhost:8089/products`
- **API Documentation**: `http://localhost:8089/swagger-ui.html`

## 🔧 Các tính năng đã implement

### Backend (Spring Boot)

- ✅ **Product Entity** - Entity đầy đủ với các trường cần thiết
- ✅ **ProductRepository** - Repository với các query phức tạp
- ✅ **ProductService & ProductServiceImpl** - Service layer hoàn chỉnh
- ✅ **ProductAPIController** - API REST đầy đủ CRUD operations
- ✅ **ProductController** - Controller cho frontend
- ✅ **CategoryAPIController** - API CRUD cho Category

### Frontend (Thymeleaf + Bootstrap)

- ✅ **Product Templates** - list.html, addOrEdit.html
- ✅ **Category Templates** - list.html, addOrEdit.html
- ✅ **AJAX Implementation** - ajax-utils.js với đầy đủ chức năng
- ✅ **AJAX Category Template** - list-ajax.html với modal
- ✅ **Navigation Updates** - Header và Dashboard đã cập nhật

### Security & Configuration

- ✅ **SecurityConfig** - Đã cập nhật để cho phép truy cập /products/\*\*
- ✅ **Session Management** - Quản lý session đúng cách
- ✅ **Error Handling** - Xử lý lỗi đăng nhập

## 🎯 API Endpoints

### Category API

- `GET /api/category` - Lấy tất cả danh mục
- `POST /api/category/getCategory` - Lấy danh mục theo ID
- `POST /api/category/addCategory` - Thêm danh mục mới
- `PUT /api/category/updateCategory` - Cập nhật danh mục
- `DELETE /api/category/deleteCategory` - Xóa danh mục

### Product API

- `GET /api/product` - Lấy tất cả sản phẩm
- `POST /api/product/getProduct` - Lấy sản phẩm theo ID
- `POST /api/product/addProduct` - Thêm sản phẩm mới
- `PUT /api/product/updateProduct` - Cập nhật sản phẩm
- `DELETE /api/product/deleteProduct` - Xóa sản phẩm
- `POST /api/product/updateStock` - Cập nhật tồn kho
- `POST /api/product/changeStatus` - Thay đổi trạng thái

## 🐛 Khắc phục sự cố

### Lỗi chuyển hướng đến trang login

- Đã sửa SecurityConfig để xử lý session đúng cách
- Đã thêm các static resources vào permitAll

### Lỗi phải đăng nhập 2 lần

- Đã cập nhật session management
- Đã thêm proper logout handling

### Lỗi không tìm thấy user

- Đã tạo DataInitController để khởi tạo dữ liệu mẫu
- Đã tạo RoleRepository

## 📝 Ghi chú

- Project sử dụng SQL Server database
- Port mặc định: 8089
- File upload được lưu trong thư mục `uploads/`
- CSRF được tắt cho API endpoints
