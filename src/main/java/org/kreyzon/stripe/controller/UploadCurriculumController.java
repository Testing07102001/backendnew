package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.CurriculumFeedback;
import org.kreyzon.stripe.entity.RegistrationAdvance;
import org.kreyzon.stripe.entity.StudentFeedback;
import org.kreyzon.stripe.entity.UploadCurriculum;
import org.kreyzon.stripe.exception.VenueNotFoundException;
import org.kreyzon.stripe.repository.CurriculumFeedbackRepository;
import org.kreyzon.stripe.repository.RegistrationAdvanceRepository;
import org.kreyzon.stripe.repository.UploadCurriculumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RestController
@CrossOrigin(origins = "https://impulz-lms.com")
public class UploadCurriculumController {
    @Autowired
    private UploadCurriculumRepository uploadCurriculumRepository;

    @Autowired
    private CurriculumFeedbackRepository curriculumFeedbackRepository;

    @Autowired
    private RegistrationAdvanceRepository registrationAdvanceRepository;


//    @PostMapping("/uploadcurriculum")
//    public ResponseEntity<UploadCurriculum> newUploadCurriculum(@RequestBody UploadCurriculum newUploadCurriculum) {
//        UploadCurriculum uploadCurriculum =(UploadCurriculum)this.uploadCurriculumRepository.save(newUploadCurriculum);
//        return ResponseEntity.status(HttpStatus.CREATED).body(uploadCurriculum);
//    }


    @PostMapping("/uploadcurriculum")
    public ResponseEntity<UploadCurriculum> newUploadCurriculum(@RequestBody UploadCurriculum newUploadCurriculum) {
        // Save the new curriculum upload
        UploadCurriculum savedUploadCurriculum = uploadCurriculumRepository.save(newUploadCurriculum);

        // Fetch student IDs based on courseId from RegistrationAdvance
        List<RegistrationAdvance> registrations = registrationAdvanceRepository.findByBatchId(newUploadCurriculum.getCourse_id());

        List<Long> studentIds = registrations.stream()
                .map(registration -> registration.getStudent().getId()) // Assuming a getStudent() method exists
                .collect(Collectors.toList());

        // Create and save curriculum feedback for each student
        for (Long studentId : studentIds) {
            CurriculumFeedback curriculumFeedback = new CurriculumFeedback();
            curriculumFeedback.setCourseId(newUploadCurriculum.getCourse_id());
            curriculumFeedback.setStudentId(studentId);
            curriculumFeedback.setSectionName(newUploadCurriculum.getSectionName()); // Assuming you have a section name in UploadCurriculum
            curriculumFeedback.setSelectCurriculum(newUploadCurriculum.getSelectCurriculum()); // Assuming this is part of UploadCurriculum
            curriculumFeedback.setStatus(false); // Initialize status as needed

            curriculumFeedbackRepository.save(curriculumFeedback);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUploadCurriculum);
    }

    @GetMapping("/getall-curicullum-feedbacks")
    public List<CurriculumFeedback> getAllCuricullumFeedbacks() {
        return  curriculumFeedbackRepository.findAll();
    }

    @GetMapping("/get-curriculum-feedbacks-bybatchid-and-studentid/{courseId}/{studentId}")
    public List<CurriculumFeedback> getCuricullumByBatchAndStudentId(@PathVariable long courseId,@PathVariable long studentId) {
        return curriculumFeedbackRepository.findByCourseIdAndStudentId(courseId, studentId);
    }


    @PutMapping("update-feedback-seen-status/{id}/{courseId}/{studentId}/{status}")
    public ResponseEntity<CurriculumFeedback> updateStatus(
            @PathVariable Long id,
            @PathVariable Long courseId,
            @PathVariable Long studentId,
            @PathVariable boolean status) {

        CurriculumFeedback feedback = curriculumFeedbackRepository.findByIdAndCourseIdAndStudentId(id, courseId, studentId);

        if (feedback == null) {
            return ResponseEntity.notFound().build(); // Return 404 if not found
        }

        feedback.setStatus(status); // Update the status
        curriculumFeedbackRepository.save(feedback); // Save the updated feedback

        return ResponseEntity.ok(feedback); // Return the updated feedback
    }


    @GetMapping("/uploadcurriculums")
    public ResponseEntity<List<UploadCurriculum>> getallUploadCurriculum(){
        List<UploadCurriculum> uploadCurriculum = this.uploadCurriculumRepository.findAll();
        return ResponseEntity.ok(uploadCurriculum);
    }

    @GetMapping("/uploadcurriculum/{id}")
    public ResponseEntity<UploadCurriculum> getUploadCurriculumById (@PathVariable Long id){
        UploadCurriculum uploadCurriculum = (UploadCurriculum)this.uploadCurriculumRepository.findById(id).orElseThrow(()->{
            return new VenueNotFoundException(id);
        });
        return ResponseEntity.ok(uploadCurriculum);
    }

    @PutMapping("/uploadcurriculum/{id}")
    public ResponseEntity<UploadCurriculum> updateUploadCurriculum(@RequestBody UploadCurriculum newUploadCurriculum, @PathVariable Long id){
        UploadCurriculum uploadCurriculum = (UploadCurriculum)this.uploadCurriculumRepository.findById(id).map((existingUploadCurriculum)->{
            existingUploadCurriculum.setSectionName(newUploadCurriculum.getSectionName());
            existingUploadCurriculum.setSelectCurriculum(newUploadCurriculum.getSelectCurriculum());
            existingUploadCurriculum.setFor_access(newUploadCurriculum.getFor_access());
            existingUploadCurriculum.setType(newUploadCurriculum.getType());
            existingUploadCurriculum.setPublicReview(newUploadCurriculum.isPublicReview());
            existingUploadCurriculum.setFileUrl(newUploadCurriculum.getFileUrl());
            existingUploadCurriculum.setCurriculum_id(newUploadCurriculum.getCurriculum_id());
            existingUploadCurriculum.setCourse_id(newUploadCurriculum.getCourse_id());
            return (UploadCurriculum)this.uploadCurriculumRepository.save(existingUploadCurriculum);
        }).orElseThrow(()->{
            return new VenueNotFoundException(id);
        });
        return ResponseEntity.ok(uploadCurriculum);
    }

    @DeleteMapping("/uploadcurriculum/{id}")
    public ResponseEntity<String> deleteUploadCurriculum(@PathVariable Long id){
        if(!this.uploadCurriculumRepository.existsById(id)){
            throw new VenueNotFoundException(id);
        } else {
            this.uploadCurriculumRepository.deleteById(id);
            return ResponseEntity.ok("upload curriculum with ID "+id+ " has been deleted successfully.");
        }
    }
}
