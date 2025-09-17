package vn.hcmute.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.hcmute.entity.Category;
import vn.hcmute.repository.CategoryRepository;
import vn.hcmute.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository repo;

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public Page<Category> search(String keyword, Pageable pageable) {
        return repo.findByCategoryNameContaining(keyword, pageable);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public Category save(Category category) {
        return repo.save(category);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
