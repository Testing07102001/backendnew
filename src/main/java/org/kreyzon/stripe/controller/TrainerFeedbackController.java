package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.TrainerFeedback;
import org.kreyzon.stripe.exception.VenueNotFoundException;
import org.kreyzon.stripe.repository.TrainerFeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Service
@RestController
@CrossOrigin(origins = "https://impulz-lms.com")
public class TrainerFeedbackController {
    @Autowired
    private TrainerFeedbackRepository trainerFeedbackRepository;

    @PostMapping({"/trainerfeedback"})
    public TrainerFeedback newTrainerFeedback (@RequestBody TrainerFeedback newTrainerFeedback){
        return (TrainerFeedback)this.trainerFeedbackRepository.save(newTrainerFeedback);
    }

    @GetMapping({"/trainerfeedbacks"})
    public List<TrainerFeedback> getAllTrainerFeedback(){
        return this.trainerFeedbackRepository.findAll();
    }

    @GetMapping({"/trainerfeedback/{id}"})
    public TrainerFeedback getTrainerFeedbackById(@PathVariable Long id){
        return (TrainerFeedback)this.trainerFeedbackRepository.findById(id).orElseThrow(()->{
            return new VenueNotFoundException(id);
        });
    }

    @PutMapping({"/trainerfeedback/{id}"})
    public ResponseEntity<TrainerFeedback> updateTrainerFeedback(@RequestBody TrainerFeedback newTrainerFeedback, @PathVariable Long id){
        TrainerFeedback trainerFeedback = (TrainerFeedback)this.trainerFeedbackRepository.findById(id).map((existingTrainerFeedback)->{
            existingTrainerFeedback.setQuestions(newTrainerFeedback.getQuestions());
            existingTrainerFeedback.setType(newTrainerFeedback.getType());
            return (TrainerFeedback)this.trainerFeedbackRepository.save(existingTrainerFeedback);
        }).orElseThrow(()->{
            return new VenueNotFoundException(id);
        });
        return ResponseEntity.ok(trainerFeedback);
    }

    @DeleteMapping({"/trainerfeedback/{id}"})
    public ResponseEntity<String> deleteTrainerFeedback(@PathVariable Long id){
        if(!this.trainerFeedbackRepository.existsById(id)){
            throw new VenueNotFoundException(id);
        }else {
            this.trainerFeedbackRepository.deleteById(id);
            return ResponseEntity.ok("Trainer feedback with ID " + id + " has been deleted successfully.");

        }
    }
}
