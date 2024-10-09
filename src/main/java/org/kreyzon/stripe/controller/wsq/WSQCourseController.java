package org.kreyzon.stripe.controller.wsq;

import org.kreyzon.stripe.dto.WSQ.*;
import org.kreyzon.stripe.entity.wsq.WSQCourse;
import org.kreyzon.stripe.service.wsq.WSQCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/wsq/courses")
public class WSQCourseController {
    @Autowired
    private WSQCourseService courseService;

    @PostMapping
    public ResponseEntity<WSQCourseResponseDTO> createCourse(@RequestBody WSQCourseRequestDTO courseRequestDTO) {
        WSQCourseResponseDTO createdCourse = courseService.createCourse(courseRequestDTO);
        return ResponseEntity.ok(createdCourse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WSQCourse> getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}")
    public ResponseEntity<WSQCourse> updateCourse(@PathVariable Long id, @RequestBody WSQCourseRequestDTO courseDetails) {
        WSQCourse updatedCourse = courseService.updateCourse(id, courseDetails);
        return ResponseEntity.ok(updatedCourse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        // Check if the category exists
        if (!courseService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Category with ID " + id + " not found.");
        }

        // If it exists, proceed to delete it
        courseService.deleteCourseById(id);
        return ResponseEntity.ok("Category with ID " + id + " deleted successfully.");
    }

    @GetMapping("/finalls")
    public List<WSQCourseDTO> getAllCourse() {
        return courseService.getAllCourses().stream()
                .map(courses -> new WSQCourseDTO(courses.getId(), courses.getName())) // Fix: close the parentheses here
                .collect(Collectors.toList()); // Then collect the stream into a list
    }

}
