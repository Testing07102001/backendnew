package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.Facilitator;
import org.kreyzon.stripe.exception.VenueNotFoundException;
import org.kreyzon.stripe.repository.FacilitatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://impulz-lms.com")
public class FacilitatorController {
    @Autowired
    private FacilitatorRepository facilitatorRepository;

    public FacilitatorController() {
    }

    @PostMapping({"/facilitator"})
    public Facilitator newFacilitator(@RequestBody Facilitator newFacilitator) {
        return (Facilitator)this.facilitatorRepository.save(newFacilitator);
    }

    @GetMapping({"/facilitators"})
    public List<Facilitator> getAllFacilitators() {
        return this.facilitatorRepository.findAll();
    }

    @GetMapping({"/facilitator/{id}"})
    Facilitator getFacilitatorById(@PathVariable Long id) {
        return (Facilitator)this.facilitatorRepository.findById(id).orElseThrow(() -> {
            return new VenueNotFoundException(id);
        });
    }

    @PutMapping({"/facilitator/{id}"})
    Facilitator updateFacilitator(@RequestBody Facilitator newFacilitator, @PathVariable Long id) {
        return (Facilitator)this.facilitatorRepository.findById(id).map((facilitator) -> {
            facilitator.setSalutation(newFacilitator.getSalutation());
            facilitator.setNric(newFacilitator.getNric());
            facilitator.setProoftype(newFacilitator.getProoftype());
            facilitator.setEmail(newFacilitator.getEmail());
            facilitator.setPhone(newFacilitator.getPhone());
            facilitator.setEducational_level(newFacilitator.getEducational_level());
            facilitator.setQualification(newFacilitator.getQualification());
            facilitator.setDomain(newFacilitator.getDomain());
            facilitator.setDomain(newFacilitator.getGender());
            facilitator.setGender(newFacilitator.getGender());
            facilitator.setPassword(newFacilitator.getPassword());
            facilitator.setName(newFacilitator.getName());
            facilitator.setUsertype(newFacilitator.getUsertype());
            return (Facilitator)this.facilitatorRepository.save(facilitator);
        }).orElseThrow(() -> {
            return new VenueNotFoundException(id);
        });
    }

    @DeleteMapping({"/facilitator/{id}"})
    String deleteFacilitator(@PathVariable Long id) {
        if (!this.facilitatorRepository.existsById(id)) {
            throw new VenueNotFoundException(id);
        } else {
            this.facilitatorRepository.deleteById(id);
            return "Facilitator with id " + id + "has been deleted success.";
        }
    }

    @GetMapping("/facilitator/count")
    public Long getStudentCount() {
        return facilitatorRepository.count();
    }

}
