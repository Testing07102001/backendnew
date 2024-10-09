package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.Course;
import org.kreyzon.stripe.exception.VenueNotFoundException;
import org.kreyzon.stripe.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RestController
@CrossOrigin(origins = "https://impulz-lms.com")
public class CourseController {
    @Autowired
    private CourseRepository courseRepository;

    public CourseController() {
    }

    @PostMapping({"/course"})
    public ResponseEntity<Course> newCourse(@RequestBody Course newCourse) {
        Course course = (Course)this.courseRepository.save(newCourse);
        return ResponseEntity.status(HttpStatus.CREATED).body(course);

    }
    @CrossOrigin(origins = "https://impulz-lms.com")
    @GetMapping({"/courses"})
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = this.courseRepository.findAll();
        return ResponseEntity.ok(courses);

    }
    @CrossOrigin(origins = "https://impulz-lms.com")
    @GetMapping({"/course/{id}"})
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        Course course = (Course)this.courseRepository.findById(id).orElseThrow(() -> {
            return new VenueNotFoundException(id);
        });
        return ResponseEntity.ok(course);
    }

    @PutMapping({"/course/{id}"})
    public ResponseEntity<Course> updateCourse(@RequestBody Course newCourse, @PathVariable Long id) {
        Course course = (Course)this.courseRepository.findById(id).map((existingCourse) -> {
            existingCourse.setCategory(newCourse.getCategory());
            existingCourse.setCategoryId(newCourse.getCategoryId());
            existingCourse.setName(newCourse.getName());
            existingCourse.setReference(newCourse.getReference());
//            existingCourse.setDuration(newCourse.getDuration());
//            existingCourse.setFee(newCourse.getFee());
//            existingCourse.setClustercode(newCourse.getClustercode());
//            existingCourse.setQualificationcode(newCourse.getQualificationcode());
            existingCourse.setDescription(newCourse.getDescription());
            existingCourse.setOutcome(newCourse.getOutcome());
            existingCourse.setRequirement(newCourse.getRequirement());
            existingCourse.setCourseType(newCourse.getCourseType());
//            existingCourse.setIdi(newCourse.getIdi());
            existingCourse.setUrl(newCourse.getUrl());
            existingCourse.setType(newCourse.getType());
            return (Course)this.courseRepository.save(existingCourse);
        }).orElseThrow(() -> {
            return new VenueNotFoundException(id);
        });
        return ResponseEntity.ok(course);
    }

    @DeleteMapping({"/course/{id}"})
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        if (!this.courseRepository.existsById(id)) {
            throw new VenueNotFoundException(id);
        } else {
            this.courseRepository.deleteById(id);
            return ResponseEntity.ok("Course with ID " + id + " has been deleted successfully.");
        }
    }

    @GetMapping("/course/count")
    public Long getStudentCount() {
        return courseRepository.count();
    }

    @GetMapping("/course/countByType")
    public ResponseEntity<Long> countCoursesByType(@RequestParam String type) {
        Long count = courseRepository.countByType(type);
        return ResponseEntity.ok(count);
    }


    @GetMapping("/courses/countByType")
    public ResponseEntity<Map<String, Long>> countCoursesByType() {
        List<Object[]> counts = courseRepository.countCoursesByType();

        Map<String, Long> result = new HashMap<>();
        for (Object[] count : counts) {
            String type = (String) count[0];
            Long totalCount = (Long) count[1];
            result.put(type, totalCount);
        }

        return ResponseEntity.ok(result);
    }
}

