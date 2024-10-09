package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.ARAssessment;
import org.kreyzon.stripe.service.ARAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessments")
@CrossOrigin(origins = "https://impulz-lms.com")
public class ARAssessmentController {
    private final ARAssessmentService assessmentService;

    @Autowired
    public ARAssessmentController(ARAssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @PostMapping
    public ARAssessment saveAssessment(@RequestBody ARAssessment assessment) {
        return assessmentService.saveAssessment(assessment);
    }

    @GetMapping
    public List<ARAssessment> getAllAssessments() {
        return assessmentService.getAllAssessments();
    }

    @GetMapping("/{id}")
    public ARAssessment getAssessmentById(@PathVariable Long id) {
        return assessmentService.getAssessmentById(id);
    }


    @PutMapping("/{id}")
    public ARAssessment updateAssessment(@PathVariable Long id, @RequestBody ARAssessment updatedAssessment) {
        return assessmentService.updateAssessment(id, updatedAssessment);
    }

    @DeleteMapping("/{id}")
    public String deleteAssessment(@PathVariable Long id) {
        assessmentService.deleteAssessment(id);
        return "Assessment with ID " + id + " deleted successfully.";
    }

    @GetMapping("/sid/{sid}")
    public List<ARAssessment> getAssessmentsBySid(@PathVariable Long sid) {
        return assessmentService.getAssessmentsBySid(sid);
    }

    @GetMapping("/sid/{sid}/bid/{bid}")
    public ARAssessment getAssessmentBySidAndBid(@PathVariable Long sid, @PathVariable Long bid) {
        List<ARAssessment> assessments = assessmentService.getAssessmentsBySid(sid);
        return assessments.stream()
                .filter(assessment -> assessment.getbId().equals(bid))
                .findFirst()
                .orElse(null);
    }


}
