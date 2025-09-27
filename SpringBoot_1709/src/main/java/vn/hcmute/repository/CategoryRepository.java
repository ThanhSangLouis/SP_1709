package vn.hcmute.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Existing method
    Page<Category> findByCategoryNameContaining(String name, Pageable pageable);
    
    // New methods for API
    Optional<Category> findByCategoryName(String name);
    List<Category> findByCategoryNameContaining(String name);
}
