IF DB_ID('LTWEB012') IS NULL
BEGIN
    CREATE DATABASE LTWEB012;
END
GO
USE LTWEB012;
GO
CREATE TABLE categories (
    category_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    category_name NVARCHAR(255) NOT NULL,
    category_image NVARCHAR(255) NULL
);
-- Bảng role
CREATE TABLE roles (
    role_id INT IDENTITY(1,1) PRIMARY KEY,
    role_name NVARCHAR(50) NOT NULL
);

-- Bảng user
CREATE TABLE users (
    user_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    full_name NVARCHAR(100),
    role_id INT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

-- Thêm dữ liệu mẫu
INSERT INTO roles (role_name) VALUES ('ADMIN'), ('USER');

INSERT INTO users (username, password, full_name, role_id)
VALUES 
('admin', '1', N'Quản trị viên', 1),
('user1', '1', N'Người dùng thường', 2);


USE LTWEB012;
GO

-- Cập nhật password admin thành BCrypt hash mới
UPDATE users 
SET password = '$2a$10$h4oq2JPtqrtkehv3QTaQ0uE6/eTTXJ7ox0Vdmo/cITT01UPAWsfZW'
WHERE username = 'admin';

-- Cập nhật password user1 thành BCrypt hash mới
UPDATE users 
SET password = '$2a$10$h4oq2JPtqrtkehv3QTaQ0uE6/eTTXJ7ox0Vdmo/cITT01UPAWsfZW'
WHERE username = 'user1';

-- Kiểm tra dữ liệu
SELECT u.username, u.password, u.full_name, r.role_name 
FROM users u 
JOIN roles r ON u.role_id = r.role_id;