package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.GeneralFeedbackStudent;
import org.kreyzon.stripe.repository.GeneralFeedbackCreateRepository;
import org.kreyzon.stripe.repository.GeneralFeedbackStudentRepository;
import org.kreyzon.stripe.repository.RegistrationAdvanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/generalfeedback")
public class GeneralFeedbackStudentController {

    @Autowired
    private GeneralFeedbackStudentRepository generalFeedbackStudentRepository;
@Autowired
private GeneralFeedbackCreateRepository generalFeedbackCreateRepository;
@Autowired
private RegistrationAdvanceRepository registrationAdvanceRepository;
    @GetMapping("/")
    public List<GeneralFeedbackStudent> getAllGeneralFeedbackStudents() {
        return generalFeedbackStudentRepository.findAll();
    }

    @GetMapping("/{id}")
    public GeneralFeedbackStudent getGeneralFeedbackStudentById(@PathVariable(value = "id") Long generalFeedbackStudentId) {
        return generalFeedbackStudentRepository.findById(generalFeedbackStudentId)
                .orElseThrow(() -> new RuntimeException("GeneralFeedbackStudent not found with id: " + generalFeedbackStudentId));
    }

    @PostMapping("/")
    public GeneralFeedbackStudent createGeneralFeedbackStudent(@RequestBody GeneralFeedbackStudent generalFeedbackStudent) {
        return generalFeedbackStudentRepository.save(generalFeedbackStudent);
    }

    @PutMapping("/{id}")
    public GeneralFeedbackStudent updateGeneralFeedbackStudent(@PathVariable(value = "id") Long generalFeedbackStudentId,
                                                               @RequestBody GeneralFeedbackStudent generalFeedbackStudentDetails) {
        GeneralFeedbackStudent generalFeedbackStudent = generalFeedbackStudentRepository.findById(generalFeedbackStudentId)
                .orElseThrow(() -> new RuntimeException("GeneralFeedbackStudent not found with id: " + generalFeedbackStudentId));

        generalFeedbackStudent.setFeedbackId(generalFeedbackStudentDetails.getFeedbackId());
        generalFeedbackStudent.setStudentId(generalFeedbackStudentDetails.getStudentId());
        generalFeedbackStudent.setBatchId(generalFeedbackStudentDetails.getBatchId());
        generalFeedbackStudent.setStudentName(generalFeedbackStudentDetails.getStudentName());
        generalFeedbackStudent.setQuestions(generalFeedbackStudentDetails.getQuestions());
        generalFeedbackStudent.setAnswers(generalFeedbackStudentDetails.getAnswers());

        return generalFeedbackStudentRepository.save(generalFeedbackStudent);
    }

    @DeleteMapping("/{id}")
    public void deleteGeneralFeedbackStudent(@PathVariable(value = "id") Long generalFeedbackStudentId) {
        GeneralFeedbackStudent generalFeedbackStudent = generalFeedbackStudentRepository.findById(generalFeedbackStudentId)
                .orElseThrow(() -> new RuntimeException("GeneralFeedbackStudent not found with id: " + generalFeedbackStudentId));

        generalFeedbackStudentRepository.delete(generalFeedbackStudent);
    }

    @GetMapping("/batch/{batchId}")
    public List<GeneralFeedbackStudent> getFeedbackDetailsByBatchId(@PathVariable(value = "batchId") Long batchId) {
        return generalFeedbackStudentRepository.findByBatchId(batchId);
    }

    @GetMapping("/student/{studentId}")
    public List<GeneralFeedbackStudent> getFeedbackAnswersByStudentId(@PathVariable(value = "studentId") Long studentId) {
        return generalFeedbackStudentRepository.findByStudentId(studentId);
    }

    @GetMapping("/feedback/{feedbackId}")
    public List<GeneralFeedbackStudent> getFeedbackByFeedbackId(@PathVariable(value = "feedbackId") Long feedbackId) {
        return generalFeedbackStudentRepository.findByFeedbackId(feedbackId);
    }
    @GetMapping("/stats")
    public Map<String, Long> getFeedbackStats() {
        List<GeneralFeedbackStudent> feedbackList = generalFeedbackStudentRepository.findAll();
        Map<String, Long> counts = new HashMap<>();

        // Initialize counts
        counts.put("Poor(20%)", 0L);
        counts.put("Fair(40%)", 0L);
        counts.put("Satisfactory(60%)", 0L);
        counts.put("Very Good(80%)", 0L);
        counts.put("Excellent(90%)", 0L);

        for (GeneralFeedbackStudent feedback : feedbackList) {
            for (String answer : feedback.getAnswers()) {
                System.out.println("Processing answer: " + answer); // Debug log
                counts.put(answer, counts.get(answer) + 1);
            }
        }

        return counts;
    }

    @GetMapping("/gettotalcount")
    public ResponseEntity<Map<String, Long>> getAllGeneralFeedbackStudentsCount() {
        long questionsCount = generalFeedbackCreateRepository.count();
        long feedbacksCount = generalFeedbackStudentRepository.count();
        long studentsCount = registrationAdvanceRepository.count();

        // Calculate unanswered count
        long unansweredCount = (questionsCount * studentsCount) - feedbacksCount;

        // Create a response map
        Map<String, Long> response = new HashMap<>();
        response.put("Answered Feedback Count", feedbacksCount);
        response.put("unanswered Feedback Count", unansweredCount);

        return ResponseEntity.ok(response);
    }

}
