package vn.hcmute.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.hcmute.entity.Product;
import vn.hcmute.entity.Category;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    
    // Basic CRUD operations
    Page<Product> findAll(Pageable pageable);
    List<Product> findAll();
    Optional<Product> findById(Long id);
    Product save(Product product);
    void deleteById(Long id);
    void delete(Product product);
    long count();
    
    // Search operations
    Page<Product> search(String keyword, Pageable pageable);
    Page<Product> findByCategory(Category category, Pageable pageable);
    Page<Product> findByStatus(Product.ProductStatus status, Pageable pageable);
    Page<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Page<Product> findInStock(Pageable pageable);
    Page<Product> findOutOfStock(Pageable pageable);
    
    // Advanced search with multiple filters
    Page<Product> findWithFilters(String keyword, Long categoryId, Product.ProductStatus status, 
                                 BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    // Business logic methods
    Optional<Product> findByProductName(String productName);
    Optional<Product> findByProductNameAndProductIdNot(String productName, Long productId);
    List<Product> findLatestProducts(Pageable pageable);
    
    // Statistics
    long countByCategory(Category category);
    long countByStatus(Product.ProductStatus status);
    
    // Stock management
    boolean updateStock(Long productId, Integer quantity);
    boolean decreaseStock(Long productId, Integer quantity);
    boolean increaseStock(Long productId, Integer quantity);
    
    // Status management
    boolean activateProduct(Long productId);
    boolean deactivateProduct(Long productId);
    boolean setOutOfStock(Long productId);
}
