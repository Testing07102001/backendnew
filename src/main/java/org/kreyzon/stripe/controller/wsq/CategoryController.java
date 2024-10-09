package org.kreyzon.stripe.controller.wsq;


import org.kreyzon.stripe.dto.WSQ.WSQCategoryDTO;
import org.kreyzon.stripe.entity.wsq.Category;
import org.kreyzon.stripe.service.wsq.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        // Set default createdTime and courseCount values
        category.setCreatedTime(LocalDateTime.now());
        category.setCourseCount(0);

        // Save the category using the service
        Category savedCategory = categoryService.saveCategory(category);

        // Return the saved category as the response
        return ResponseEntity.ok(savedCategory);
    }



    // Retrieve all Categories
    @GetMapping
    public Page<Category> getCategories(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return categoryService.getCategories(name, page, size);
    }


    // Retrieve a Category by ID
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Optional<Category> category = categoryService.getCategoryById(id);
        return category.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update a Category
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
        Optional<Category> optionalCategory = categoryService.getCategoryById(id);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
//            category.setCourseCount(categoryDetails.getCourseCount());
            category.setCreatedTime(categoryDetails.getCreatedTime());
            category.setName(categoryDetails.getName());
            Category updatedCategory = categoryService.saveCategory(category);
            return ResponseEntity.ok(updatedCategory);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a Category
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        // Check if the category exists
        if (!categoryService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Category with ID " + id + " not found.");
        }

        // If it exists, proceed to delete it
        categoryService.deleteCategoryById(id);
        return ResponseEntity.ok("Category with ID " + id + " deleted successfully.");
    }
    @GetMapping("/findall")
    public ResponseEntity<List<WSQCategoryDTO>> getAllCategories() {
        List<Category> categories = categoryService.findAll(); // Assuming you have a method to fetch all categories

        // Map to DTOs
        List<WSQCategoryDTO> categoryDTOs = categories.stream()
                .map(category -> new WSQCategoryDTO(category.getId(), category.getName()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(categoryDTOs);
    }
}