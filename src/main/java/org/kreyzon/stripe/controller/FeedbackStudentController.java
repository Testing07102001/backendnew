package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.FeedbackStudent;
import org.kreyzon.stripe.exception.VenueNotFoundException;
import org.kreyzon.stripe.repository.StudentFeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Service
@RestController
@CrossOrigin(origins = "https://impulz-lms.com")
public class FeedbackStudentController {
    @Autowired
    private StudentFeedbackRepository studentFeedbackRepository;

    public FeedbackStudentController(){

    }

    @PostMapping({"/studentfeedback"})
    public FeedbackStudent newFeedbackStudent (@RequestBody FeedbackStudent newFeedbackStudent){
        return (FeedbackStudent)this.studentFeedbackRepository.save(newFeedbackStudent);
    }

    @GetMapping({"/studentfeedbacks"})
    public List<FeedbackStudent> getAllFeedbackStudent(){
        return this.studentFeedbackRepository.findAll();
    }

    @GetMapping({"/studentfeedback/{id}"})
    public FeedbackStudent getFeedbackStudentById(@PathVariable Long id){
        return (FeedbackStudent)this.studentFeedbackRepository.findById(id).orElseThrow(()->{
            return new VenueNotFoundException(id);
        });
    }
    @PutMapping({"/studentfeedback/{id}"})
    public ResponseEntity<FeedbackStudent> updateFeedbackStudent(@RequestBody FeedbackStudent newFeedbackStudent, @PathVariable Long id){
        FeedbackStudent feedbackStudent = (FeedbackStudent)this.studentFeedbackRepository.findById(id).map((existingFeedbackStudent)->{
            existingFeedbackStudent.setQuestions(newFeedbackStudent.getQuestions());
            existingFeedbackStudent.setType(newFeedbackStudent.getType());
            existingFeedbackStudent.setBatchId(newFeedbackStudent.getBatchId());
            return (FeedbackStudent)this.studentFeedbackRepository.save(existingFeedbackStudent);
        }).orElseThrow(()->{
            return new VenueNotFoundException(id);
        });
        return ResponseEntity.ok(feedbackStudent);
    }

    @DeleteMapping({"/studentfeedback/{id}"})
    public ResponseEntity<String> deleteFeedbackStudent(@PathVariable Long id){
        if(!this.studentFeedbackRepository.existsById(id)){
            throw new VenueNotFoundException(id);
        } else {
            this.studentFeedbackRepository.deleteById(id);
            return ResponseEntity.ok("feedback with ID " + id + " has been deleted successfully.");
        }
    }


}
