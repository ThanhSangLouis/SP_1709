package vn.hcmute.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hcmute.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Page<Category> findByCategoryNameContaining(String name, Pageable pageable);
}
