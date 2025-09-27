package vn.hcmute.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import vn.hcmute.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    // Existing methods
    Page<Category> findAll(Pageable pageable);
    Page<Category> search(String keyword, Pageable pageable);
    Optional<Category> findById(Long id);
    Category save(Category category);
    void deleteById(Long id);
    
    // New methods for API
    Optional<Category> findByCategoryName(String name);
    List<Category> findAll();
    List<Category> findAll(Sort sort);
    List<Category> findAllById(Iterable<Long> ids);
    long count();
    void delete(Category entity);
    List<Category> findByCategoryNameContaining(String name);
    Page<Category> findByCategoryNameContaining(String name, Pageable pageable);
}