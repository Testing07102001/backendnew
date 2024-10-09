package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.SFReceive;
import org.kreyzon.stripe.exception.VenueNotFoundException;
import org.kreyzon.stripe.repository.SFReceiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://impulz-lms.com")
public class SFReceiveController {
    @Autowired
    private SFReceiveRepository sfReceiveRepository;

    @PostMapping({"/sfreceice"})
    public SFReceive newSFReceive(@RequestBody SFReceive newSFReceive){
        return (SFReceive)this.sfReceiveRepository.save(newSFReceive);
    }

    @GetMapping({"/sfreceices"})
    public List<SFReceive> getAllUsers(){
        return this.sfReceiveRepository.findAll();
    }

    @GetMapping({"/sfreceice/{id}"})
    public SFReceive getUserbyId(@PathVariable Long id){
        return (SFReceive)this.sfReceiveRepository.findById(id).orElseThrow(()->{
            return new VenueNotFoundException(id);
        });
    }

    @PutMapping({"/sfreceice/{id}"})
    public SFReceive updateSFReceive(@RequestBody SFReceive newSFReceive, @PathVariable Long id){
        return (SFReceive)this.sfReceiveRepository.findById(id).map((SFReceive)->{
            SFReceive.setBatchId(newSFReceive.getBatchId());
            SFReceive.setQuestion(newSFReceive.getQuestion());
            SFReceive.setType(newSFReceive.getType());
            SFReceive.setFeedbackId(newSFReceive.getFeedbackId());
            SFReceive.setStudentId(newSFReceive.getStudentId());
            SFReceive.setAnswerQ(newSFReceive.getAnswerQ());
            SFReceive.setAnswerT(newSFReceive.getAnswerT());
            return (SFReceive)this.sfReceiveRepository.save(SFReceive);
        }).orElseThrow(()->{
            return new VenueNotFoundException(id);
        });
    }

    @DeleteMapping({"/sfreceice/{id}"})
    public String deleteSFReceive(@PathVariable Long id){
        if(!this.sfReceiveRepository.existsById(id)){
            throw new VenueNotFoundException(id);
        } else {
            this.sfReceiveRepository.deleteById(id);
            return "Feedback with id "+id+" has been deleted successfully.";
        }
    }
}
