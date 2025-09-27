package vn.hcmute.entity;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "categories")
@Schema(description = "Thông tin danh mục sản phẩm")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID danh mục", example = "1")
    private Long categoryId;

    @Schema(description = "Tên danh mục", example = "Điện thoại")
    private String categoryName;

    @Schema(description = "Đường dẫn hình ảnh danh mục", example = "p12345678-1234-1234-1234-123456789abc.jpg")
    private String categoryImage;

    // Constructors
    public Category() {}

    public Category(String categoryName, String categoryImage) {
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
    }

    // Getters and Setters
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }
}