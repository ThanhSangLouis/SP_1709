package vn.hcmute.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import vn.hcmute.entity.Category;
import vn.hcmute.repository.CategoryRepository;
import vn.hcmute.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository repo;

    // Existing methods
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

    // New methods for API
    @Override
    public Optional<Category> findByCategoryName(String name) {
        return repo.findByCategoryName(name);
    }

    @Override
    public List<Category> findAll() {
        return repo.findAll();
    }

    @Override
    public List<Category> findAll(Sort sort) {
        return repo.findAll(sort);
    }

    @Override
    public List<Category> findAllById(Iterable<Long> ids) {
        return repo.findAllById(ids);
    }

    @Override
    public long count() {
        return repo.count();
    }

    @Override
    public void delete(Category entity) {
        repo.delete(entity);
    }

    @Override
    public List<Category> findByCategoryNameContaining(String name) {
        return repo.findByCategoryNameContaining(name);
    }

    @Override
    public Page<Category> findByCategoryNameContaining(String name, Pageable pageable) {
        return repo.findByCategoryNameContaining(name, pageable);
    }
}
