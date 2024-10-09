package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.RequestStudent;
import org.kreyzon.stripe.exception.VenueNotFoundException;
import org.kreyzon.stripe.repository.RequestStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://impulz-lms.com")
public class RequestStudentController {
    @Autowired
    private RequestStudentRepository requestStudentRepository;

    @PostMapping({"/requeststudent"})
    public RequestStudent newRequestStudent(@RequestBody RequestStudent newRequestStudent){
        return (RequestStudent)this.requestStudentRepository.save(newRequestStudent);
    }

    @GetMapping({"/requeststudent"})
    public List<RequestStudent> getAllRequestStudent(){
        return this.requestStudentRepository.findAll();
    }
    @GetMapping({"/requeststudents/{id}"})
    public RequestStudent getRequestStudentById(@PathVariable Long id){
        return (RequestStudent)this.requestStudentRepository.findById(id).orElseThrow(()->{
            return new VenueNotFoundException(id);
        });
    }
    @PutMapping({"/requeststudents/{id}"})
    public RequestStudent updateRequestStudent(@RequestBody RequestStudent newRequestStudent, @PathVariable Long id){
        return (RequestStudent)this.requestStudentRepository.findById(id).map((requestStudent)->{
            requestStudent.setFullname(newRequestStudent.getFullname());
            requestStudent.setNric(newRequestStudent.getNric());
            requestStudent.setEmail(newRequestStudent.getEmail());
            requestStudent.setPhone(newRequestStudent.getPhone());
            requestStudent.setGender(newRequestStudent.getGender());
            requestStudent.setDob(newRequestStudent.getDob());
            requestStudent.setPassword(newRequestStudent.getPassword());
            requestStudent.setAddress(newRequestStudent.getAddress());
            requestStudent.setPostalcode(newRequestStudent.getPostalcode());
            requestStudent.setRace(newRequestStudent.getRace());
            requestStudent.setEducation(newRequestStudent.getEducation());
            requestStudent.setEmploymentstatus(newRequestStudent.getEmploymentstatus());
            requestStudent.setSalary(newRequestStudent.getSalary());
            requestStudent.setCompanyname(newRequestStudent.getCompanyname());
            requestStudent.setCorporatecompanysponsored(newRequestStudent.getCorporatecompanysponsored());
            requestStudent.setDesignation(newRequestStudent.getDesignation());
            requestStudent.setProfileUrl(newRequestStudent.getProfileUrl());
            requestStudent.setBatch(newRequestStudent.getBatch());
            requestStudent.setBatch_id(newRequestStudent.getBatch_id());
            requestStudent.setStatus(newRequestStudent.getStatus());

            return (RequestStudent)this.requestStudentRepository.save(requestStudent);
        }).orElseThrow(()->{
            return new VenueNotFoundException(id);
        });
    }
    @DeleteMapping({"/requeststudents/{id}"})
    public String deleteRequestStudent(@PathVariable Long id){
        if(!this.requestStudentRepository.existsById(id)){
            throw new VenueNotFoundException(id);
        } else{
            this.requestStudentRepository.deleteById(id);
            return "Requested student with id "+ id +" has been deleted successfully.";
        }
    }

    @GetMapping("/requeststudents/count")
    public Long getStudentCount() {
        return requestStudentRepository.count();
    }

}
