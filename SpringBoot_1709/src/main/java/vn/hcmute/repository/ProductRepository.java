package vn.hcmute.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.hcmute.entity.Product;
import vn.hcmute.entity.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Tìm kiếm sản phẩm theo tên
    Page<Product> findByProductNameContaining(String keyword, Pageable pageable);
    
    // Tìm kiếm sản phẩm theo danh mục
    Page<Product> findByCategory(Category category, Pageable pageable);
    
    // Tìm kiếm sản phẩm theo tên và danh mục
    Page<Product> findByProductNameContainingAndCategory(String keyword, Category category, Pageable pageable);
    
    // Tìm kiếm sản phẩm theo trạng thái
    Page<Product> findByStatus(Product.ProductStatus status, Pageable pageable);
    
    // Tìm kiếm sản phẩm theo khoảng giá
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(@Param("minPrice") java.math.BigDecimal minPrice, 
                                   @Param("maxPrice") java.math.BigDecimal maxPrice, 
                                   Pageable pageable);
    
    // Tìm kiếm sản phẩm còn hàng
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0")
    Page<Product> findInStock(Pageable pageable);
    
    // Tìm kiếm sản phẩm hết hàng
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= 0")
    Page<Product> findOutOfStock(Pageable pageable);
    
    // Tìm kiếm tổng quát với nhiều điều kiện
    @Query("SELECT p FROM Product p WHERE " +
           "(:keyword IS NULL OR p.productName LIKE %:keyword% OR p.description LIKE %:keyword%) AND " +
           "(:categoryId IS NULL OR p.category.categoryId = :categoryId) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> findWithFilters(@Param("keyword") String keyword,
                                  @Param("categoryId") Long categoryId,
                                  @Param("status") Product.ProductStatus status,
                                  @Param("minPrice") java.math.BigDecimal minPrice,
                                  @Param("maxPrice") java.math.BigDecimal maxPrice,
                                  Pageable pageable);
    
    // Lấy sản phẩm mới nhất
    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC")
    List<Product> findLatestProducts(Pageable pageable);
    
    // Đếm sản phẩm theo danh mục
    long countByCategory(Category category);
    
    // Đếm sản phẩm theo trạng thái
    long countByStatus(Product.ProductStatus status);
    
    // Tìm sản phẩm theo tên chính xác (để kiểm tra trùng lặp)
    Optional<Product> findByProductName(String productName);
    
    // Tìm sản phẩm theo tên và khác ID (để kiểm tra trùng lặp khi update)
    Optional<Product> findByProductNameAndProductIdNot(String productName, Long productId);
}
