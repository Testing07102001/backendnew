package org.kreyzon.stripe.service.wsq;

import org.kreyzon.stripe.entity.wsq.Category;
import org.kreyzon.stripe.repository.wsq.CategoryRepository;
import org.kreyzon.stripe.repository.wsq.WSQCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private WSQCourseRepository courseRepository;

    //    public Category saveCategory(Category category) {
//        return categoryRepository.save(category);
//    }
    // Retrieve all Categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Retrieve a Category by ID
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    // Delete a Category by ID

    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id); // Delete the category
    }

    public boolean existsById(Long id) {
        return categoryRepository.existsById(id); // Check for existence
    }

    public Page<Category> getCategories(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (name != null && !name.isEmpty()) {
            return categoryRepository.findByNameContaining(name, pageable);
        } else {
            return categoryRepository.findAll(pageable);
        }
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
