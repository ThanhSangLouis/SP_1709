 package vn.hcmute.entity;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Schema(description = "Thông tin sản phẩm")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID sản phẩm", example = "1")
    private Long productId;

    @Schema(description = "Tên sản phẩm", example = "iPhone 15 Pro")
    @Column(nullable = false, length = 255)
    private String productName;

    @Schema(description = "Mô tả sản phẩm", example = "Điện thoại thông minh cao cấp")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Schema(description = "Giá sản phẩm", example = "29990000")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Schema(description = "Số lượng tồn kho", example = "100")
    @Column(nullable = false)
    private Integer stockQuantity;

    @Schema(description = "Hình ảnh sản phẩm", example = "p12345678-1234-1234-1234-123456789abc.jpg")
    @Column(length = 500)
    private String productImage;

    @Schema(description = "Trạng thái sản phẩm", example = "ACTIVE")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.ACTIVE;

    @Schema(description = "Ngày tạo", example = "2024-01-01T10:00:00")
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Schema(description = "Ngày cập nhật", example = "2024-01-01T10:00:00")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @Schema(description = "Danh mục sản phẩm")
    private Category category;

    // Constructors
    public Product() {}

    public Product(String productName, String description, BigDecimal price, Integer stockQuantity, Category category) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Enum for Product Status
    public enum ProductStatus {
        ACTIVE("Hoạt động"),
        INACTIVE("Tạm dừng"),
        OUT_OF_STOCK("Hết hàng");

        private final String displayName;

        ProductStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
