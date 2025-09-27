package vn.hcmute.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.hcmute.entity.Product;
import vn.hcmute.entity.Category;
import vn.hcmute.repository.ProductRepository;
import vn.hcmute.service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public void delete(Product product) {
        productRepository.delete(product);
    }

    @Override
    public long count() {
        return productRepository.count();
    }

    @Override
    public Page<Product> search(String keyword, Pageable pageable) {
        return productRepository.findByProductNameContaining(keyword, pageable);
    }

    @Override
    public Page<Product> findByCategory(Category category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable);
    }

    @Override
    public Page<Product> findByStatus(Product.ProductStatus status, Pageable pageable) {
        return productRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceRange(minPrice, maxPrice, pageable);
    }

    @Override
    public Page<Product> findInStock(Pageable pageable) {
        return productRepository.findInStock(pageable);
    }

    @Override
    public Page<Product> findOutOfStock(Pageable pageable) {
        return productRepository.findOutOfStock(pageable);
    }

    @Override
    public Page<Product> findWithFilters(String keyword, Long categoryId, Product.ProductStatus status, 
                                        BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findWithFilters(keyword, categoryId, status, minPrice, maxPrice, pageable);
    }

    @Override
    public Optional<Product> findByProductName(String productName) {
        return productRepository.findByProductName(productName);
    }

    @Override
    public Optional<Product> findByProductNameAndProductIdNot(String productName, Long productId) {
        return productRepository.findByProductNameAndProductIdNot(productName, productId);
    }

    @Override
    public List<Product> findLatestProducts(Pageable pageable) {
        return productRepository.findLatestProducts(pageable);
    }

    @Override
    public long countByCategory(Category category) {
        return productRepository.countByCategory(category);
    }

    @Override
    public long countByStatus(Product.ProductStatus status) {
        return productRepository.countByStatus(status);
    }

    @Override
    public boolean updateStock(Long productId, Integer quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setStockQuantity(quantity);
            productRepository.save(product);
            return true;
        }
        return false;
    }

    @Override
    public boolean decreaseStock(Long productId, Integer quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            int newStock = product.getStockQuantity() - quantity;
            if (newStock >= 0) {
                product.setStockQuantity(newStock);
                productRepository.save(product);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean increaseStock(Long productId, Integer quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setStockQuantity(product.getStockQuantity() + quantity);
            productRepository.save(product);
            return true;
        }
        return false;
    }

    @Override
    public boolean activateProduct(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setStatus(Product.ProductStatus.ACTIVE);
            productRepository.save(product);
            return true;
        }
        return false;
    }

    @Override
    public boolean deactivateProduct(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setStatus(Product.ProductStatus.INACTIVE);
            productRepository.save(product);
            return true;
        }
        return false;
    }

    @Override
    public boolean setOutOfStock(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setStatus(Product.ProductStatus.OUT_OF_STOCK);
            productRepository.save(product);
            return true;
        }
        return false;
    }
}
