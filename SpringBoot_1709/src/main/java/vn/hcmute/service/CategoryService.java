package vn.hcmute.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.hcmute.entity.Category;

import java.util.Optional;

public interface CategoryService {
    Page<Category> findAll(Pageable pageable);
    Page<Category> search(String keyword, Pageable pageable);
    Optional<Category> findById(Long id);
    Category save(Category category);
    void deleteById(Long id);
}