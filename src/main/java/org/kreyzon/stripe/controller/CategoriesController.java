package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.Categories;
import org.kreyzon.stripe.exception.VenueNotFoundException;
import org.kreyzon.stripe.repository.CategoriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://impulz-lms.com")
public class CategoriesController {
    @Autowired
    private CategoriesRepository categoriesRepository;

    public CategoriesController() {
    }

    @PostMapping({"/category"})
    public Categories createCategories(@RequestBody Categories newCategories) {
        return (Categories)this.categoriesRepository.save(newCategories);
    }
    @CrossOrigin(origins = "https://impulz-lms.com")
    @GetMapping("/category/{id}")
    public Categories getCategoriesById(@PathVariable Long id) {
        return (Categories)this.categoriesRepository.findById(id).orElseThrow(()->{
            return new VenueNotFoundException(id);
        });
    }


    @CrossOrigin(origins = "https://impulz-lms.com")
    @GetMapping({"/categorys"})
    List<Categories> getAllcategorys() {
        return this.categoriesRepository.findAll();
    }

    @PutMapping({"/category/{id}"})
    public Categories updateCategories(@RequestBody Categories newCategories, @PathVariable Long id) {
        return (Categories)this.categoriesRepository.findById(id).map((categories) -> {
            categories.setName(newCategories.getName());
            categories.setCourse(newCategories.getCourse());
            categories.setUrl(newCategories.getUrl());
            categories.setCreatedAt(newCategories.getCreatedAt());
            return (Categories)this.categoriesRepository.save(categories);
        }).orElseThrow(() -> {
            return new VenueNotFoundException(id);
        });
    }

    @DeleteMapping({"/category/{id}"})
    public void deleteCategories(@PathVariable Long id) {
        this.categoriesRepository.deleteById(id);
    }

    @GetMapping("/category/count")
    public Long getStudentCount() {
        return categoriesRepository.count();
    }

}
