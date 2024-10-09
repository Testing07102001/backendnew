package org.kreyzon.stripe.service.wsq;

import org.kreyzon.stripe.dto.WSQ.CategoryResponseDTO;
import org.kreyzon.stripe.dto.WSQ.WSQCourseRequestDTO;
import org.kreyzon.stripe.dto.WSQ.WSQCourseResponseDTO;
import org.kreyzon.stripe.entity.wsq.Category;
import org.kreyzon.stripe.entity.wsq.WSQCourse;
import org.kreyzon.stripe.repository.wsq.CategoryRepository;
import org.kreyzon.stripe.repository.wsq.WSQCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WSQCourseService {
    @Autowired
    private WSQCourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    @Transactional
    public WSQCourseResponseDTO createCourse(WSQCourseRequestDTO courseRequestDTO) {
        // Find the category by categoryId
        Optional<Category> optionalCategory = categoryRepository.findById(courseRequestDTO.getCategoryId());
        if (!optionalCategory.isPresent()) {
            throw new IllegalArgumentException("Category not found with id: " + courseRequestDTO.getCategoryId());
        }

        // Get the category
        Category category = optionalCategory.get();

        // Create the WSQCourse
        WSQCourse course = new WSQCourse();
        course.setName(courseRequestDTO.getName());
        course.setFee(courseRequestDTO.getFee());
        course.setLanguages(courseRequestDTO.getLanguages());
        course.setCategory(category);  // Set the category

        WSQCourse savedCourse = courseRepository.save(course); // Save the course

        // Increment the course count for that category
        category.setCourseCount(category.getCourseCount() + 1);
        categoryRepository.save(category); // Save the updated category

        // Create a response category DTO
        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getCreatedTime() // Include the createdTime in the response
        );

        // Return the response DTO including the category details
        return new WSQCourseResponseDTO(
                Math.toIntExact(savedCourse.getId()),
                savedCourse.getName(),
                savedCourse.getFee(),
                savedCourse.getLanguages(),
                categoryResponseDTO // Set the response DTO for the category
        );
    }

    public List<WSQCourse> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<WSQCourse> getCourseById(Long id) {
        return courseRepository.findById(id);
    }
    public WSQCourse updateCourse(Long id, WSQCourseRequestDTO courseDetails) {
        WSQCourse course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id " + id));

        // Update fields
        course.setName(courseDetails.getName());
        course.setFee(courseDetails.getFee());

        // Fetch the category by ID and set it
        if (courseDetails.getCategoryId() != null) {
            Category category = categoryRepository.findById(courseDetails.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id " + courseDetails.getCategoryId()));
            course.setCategory(category);
        }

        // Update languages if necessary
        course.setLanguages(courseDetails.getLanguages());

        return courseRepository.save(course);
    }


    public void deleteCourseById(Long id) {
        courseRepository .deleteById(id); // Delete the category
    }
    public boolean existsById(Long id) {
        return courseRepository .existsById(id); // Check for existence
    }
    public List<WSQCourse> findAll() {
        return courseRepository.findAll();
    }
}
