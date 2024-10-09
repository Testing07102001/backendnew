package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.GeneralFeedbackCreate;
import org.kreyzon.stripe.entity.RegistrationAdvance;
import org.kreyzon.stripe.entity.StudentFeedback;
import org.kreyzon.stripe.repository.GeneralFeedbackCreateRepository;
import org.kreyzon.stripe.repository.RegistrationAdvanceRepository;
import org.kreyzon.stripe.repository.StudentFeedbackStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/generalfeedbackcreate")
public class GeneralFeedbackCreateController {

    @Autowired
    private GeneralFeedbackCreateRepository generalFeedbackCreateRepository;
    @Autowired
    private StudentFeedbackStatusRepository studentFeedbackStatusRepository;

    @Autowired
    private RegistrationAdvanceRepository registrationAdvanceRepository;

    @GetMapping("/")
    public List<GeneralFeedbackCreate> getAllGeneralFeedbackCreates() {
        return generalFeedbackCreateRepository.findAll();
    }

    @GetMapping("/{id}")
    public GeneralFeedbackCreate getGeneralFeedbackCreateById(@PathVariable(value = "id") Long generalFeedbackCreateId) {
        return generalFeedbackCreateRepository.findById(generalFeedbackCreateId)
                .orElseThrow(() -> new RuntimeException("GeneralFeedbackCreate not found with id: " + generalFeedbackCreateId));
    }

    @GetMapping("/batch/{batchId}")
    public ResponseEntity<?> getGeneralFeedbackCreateByBatchId(@PathVariable(value = "batchId") Long batchId) {
        List<GeneralFeedbackCreate> feedbackList = generalFeedbackCreateRepository.findByBatchId(batchId);

        if (feedbackList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No feedback found.");
        } else {
            return ResponseEntity.ok(feedbackList);
        }
    }


//    @PostMapping("/")
//    public GeneralFeedbackCreate createGeneralFeedbackCreate(@RequestBody GeneralFeedbackCreate generalFeedbackCreate) {
//        return generalFeedbackCreateRepository.save(generalFeedbackCreate);
//    }


    @PostMapping("/")
    public GeneralFeedbackCreate createGeneralFeedbackCreate(@RequestBody GeneralFeedbackCreate generalFeedbackCreate) {
        // Save the general feedback
        GeneralFeedbackCreate savedFeedback = generalFeedbackCreateRepository.save(generalFeedbackCreate);

        // Fetch student IDs based on batchId from RegistrationAdvance
        List<RegistrationAdvance> registrations = registrationAdvanceRepository.findByBatchId(generalFeedbackCreate.getBatchId());
        List<Long> studentIds = registrations.stream()
                .map(registration -> registration.getStudent().getId()) // Assuming a getStudent() method exists
                .collect(Collectors.toList());

        // Create and save feedback status for each student
        for (Long studentId : studentIds) {
            StudentFeedback studentFeedback = new StudentFeedback();
            studentFeedback.setBatchId(generalFeedbackCreate.getBatchId());
            studentFeedback.setStudentId(studentId);
            studentFeedback.setStatus(false); // Initialize status as needed

            studentFeedbackStatusRepository.save(studentFeedback);
        }

        return savedFeedback;
    }

    @GetMapping("/all")
    public List<StudentFeedback> getAllStudentFeedback() {
        return studentFeedbackStatusRepository.findAll(); // Fetch all entries
    }

    @GetMapping("/findByBatchAndStudentId/{batchId}/{studentId}")
    public List<StudentFeedback> getFeedbackByBatchIdAndStudentId(
            @PathVariable Long batchId,
            @PathVariable Long studentId) {
        return studentFeedbackStatusRepository.findByBatchIdAndStudentId(batchId, studentId);
    }

    @PutMapping("/{id}/batch/{batchId}/student/{studentId}/status/{status}")
    public ResponseEntity<StudentFeedback> updateStatus(
            @PathVariable Long id,
            @PathVariable Long batchId,
            @PathVariable Long studentId,
            @PathVariable boolean status) {

        StudentFeedback feedback = studentFeedbackStatusRepository.findByIdAndBatchIdAndStudentId(id, batchId, studentId);

        if (feedback == null) {
            return ResponseEntity.notFound().build(); // Return 404 if not found
        }

        feedback.setStatus(status); // Update the status
        studentFeedbackStatusRepository.save(feedback); // Save the updated feedback

        return ResponseEntity.ok(feedback); // Return the updated feedback
    }


    @PutMapping("/{id}")
    public GeneralFeedbackCreate updateGeneralFeedbackCreate(@PathVariable(value = "id") Long generalFeedbackCreateId,
                                                             @RequestBody GeneralFeedbackCreate generalFeedbackCreateDetails) {
        GeneralFeedbackCreate generalFeedbackCreate = generalFeedbackCreateRepository.findById(generalFeedbackCreateId)
                .orElseThrow(() -> new RuntimeException("GeneralFeedbackCreate not found with id: " + generalFeedbackCreateId));

        generalFeedbackCreate.setQuestions(generalFeedbackCreateDetails.getQuestions());
        generalFeedbackCreate.setBatchId(generalFeedbackCreateDetails.getBatchId());
        generalFeedbackCreate.setModuleName(generalFeedbackCreateDetails.getModuleName());
        generalFeedbackCreate.setDateTaken(generalFeedbackCreateDetails.getDateTaken());
        generalFeedbackCreate.setLectureName(generalFeedbackCreateDetails.getLectureName());

        return generalFeedbackCreateRepository.save(generalFeedbackCreate);
    }

    @DeleteMapping("/{id}")
    public void deleteGeneralFeedbackCreate(@PathVariable(value = "id") Long generalFeedbackCreateId) {
        GeneralFeedbackCreate generalFeedbackCreate = generalFeedbackCreateRepository.findById(generalFeedbackCreateId)
                .orElseThrow(() -> new RuntimeException("GeneralFeedbackCreate not found with id: " + generalFeedbackCreateId));

        generalFeedbackCreateRepository.delete(generalFeedbackCreate);
    }


}
