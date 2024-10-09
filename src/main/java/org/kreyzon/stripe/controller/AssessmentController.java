package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.Assessment;
import org.kreyzon.stripe.exception.VenueNotFoundException;
import org.kreyzon.stripe.repository.AssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://impulz-lms.com")
public class AssessmentController {
    @Autowired
    private AssessmentRepository assessmentRepository;


    @PostMapping("/assessment")
    public ResponseEntity<Assessment> newAssessment(@RequestBody Assessment newAssessment) {
        Assessment assessment = assessmentRepository.save(newAssessment);
        return ResponseEntity.status(HttpStatus.CREATED).body(assessment);
    }


    @GetMapping("/assessments")
    public ResponseEntity<List<Assessment>> getAllAssessments() {
        List<Assessment> assessments = assessmentRepository.findAll();
        return ResponseEntity.ok(assessments);
    }


    @GetMapping("/assessment/{id}")
    public ResponseEntity<Assessment> getAssessmentById(@PathVariable Long id) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new VenueNotFoundException(id));
        return ResponseEntity.ok(assessment);
    }


    @PutMapping("/assessment/{id}")
    public ResponseEntity<Assessment> updateAssessment(@RequestBody Assessment newAssessment, @PathVariable Long id) {
        Assessment assessment = assessmentRepository.findById(id)
                .map(existingAssessment -> {
                    existingAssessment.setName(newAssessment.getName());
                    existingAssessment.setCourse(newAssessment.getCourse());
                    existingAssessment.setBatch(newAssessment.getBatch());
                    existingAssessment.setDuration(newAssessment.getDuration());
                    existingAssessment.setStart(newAssessment.getStart());
                    existingAssessment.setEnd_time(newAssessment.getEnd_time());
                    existingAssessment.setDescription(newAssessment.getDescription());
                    return assessmentRepository.save(existingAssessment);
                })
                .orElseThrow(() -> new VenueNotFoundException(id));


        return ResponseEntity.ok(assessment);
    }


    @DeleteMapping("/assessment/{id}")
    public ResponseEntity<String> deleteAssessment(@PathVariable Long id) {
        if (!assessmentRepository.existsById(id)) {
            throw new VenueNotFoundException(id);
        }


        assessmentRepository.deleteById(id);
        return ResponseEntity.ok("Assessment with ID " + id + " has been deleted successfully.");
    }
}
