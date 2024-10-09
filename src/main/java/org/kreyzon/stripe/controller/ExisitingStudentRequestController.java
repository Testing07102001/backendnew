package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.ExisitingStudentRequest;
import org.kreyzon.stripe.exception.VenueNotFoundException;
import org.kreyzon.stripe.repository.ExisitingStudentRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://impulz-lms.com")
public class ExisitingStudentRequestController {
    @Autowired
    private ExisitingStudentRequestRepository exisitingStudentRequestRepository;

    @PostMapping({"/exisitingstudentrequest"})
    public ExisitingStudentRequest newExisitingStudentRequest(@RequestBody ExisitingStudentRequest newExisitingStudentRequest){
        return (ExisitingStudentRequest)this.exisitingStudentRequestRepository.save(newExisitingStudentRequest);
    }

    @GetMapping({"/exisitingstudentrequests"})
    public List<ExisitingStudentRequest> getAllExisitingStudentRequest(){
        return this.exisitingStudentRequestRepository.findAll();
    }

    @GetMapping({"/exisitingstudentrequest/{id}"})
    public ExisitingStudentRequest getExisitingStudentRequestById(@PathVariable Long id){
        return (ExisitingStudentRequest)this.exisitingStudentRequestRepository.findById(id).orElseThrow(()->{
            return new VenueNotFoundException(id);
        });
    }

    @PutMapping({"/exisitingstudentrequest/{id}"})
    public ExisitingStudentRequest updateExisitingStudentRequest(@RequestBody ExisitingStudentRequest newExisitingStudentRequest, @PathVariable Long id){
        return (ExisitingStudentRequest)this.exisitingStudentRequestRepository.findById(id).map((exisitingStudentRequest)->{
            exisitingStudentRequest.setStudentId(newExisitingStudentRequest.getStudentId());
            exisitingStudentRequest.setCourseId(newExisitingStudentRequest.getCourseId());
            exisitingStudentRequest.setStatus(newExisitingStudentRequest.getStatus());
            exisitingStudentRequest.setCourse(newExisitingStudentRequest.getCourse());
            exisitingStudentRequest.setLogId(newExisitingStudentRequest.getLogId());
            return (ExisitingStudentRequest)this.exisitingStudentRequestRepository.save(exisitingStudentRequest);
        }).orElseThrow(()->{
            return new VenueNotFoundException(id);
        });
    }

    @DeleteMapping({"/exisitingstudentrequest/{id}"})
    public String deleteExisitingStudentRequest(@PathVariable Long id){
        if(!this.exisitingStudentRequestRepository.existsById(id)){
            throw new VenueNotFoundException(id);
        }else{
            this.exisitingStudentRequestRepository.deleteById(id);
            return "Requested exisiting student details with id "+id+" has been deleted successfully.";
        }
    }
}
