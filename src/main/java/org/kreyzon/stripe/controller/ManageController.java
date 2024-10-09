package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.Manage;
import org.kreyzon.stripe.exception.VenueNotFoundException;
import org.kreyzon.stripe.repository.ManageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://impulz-lms.com")
public class ManageController {
    private final ManageRepository manageRepository;

    @Autowired
    public ManageController(ManageRepository manageRepository) {
        this.manageRepository = manageRepository;
    }

    @PostMapping({"/manage"})
    public Manage newManage(@RequestBody Manage newManage) {
        return (Manage)this.manageRepository.save(newManage);
    }



    @GetMapping({"/manages"})
    public List<Manage> getAllManage() {
        return this.manageRepository.findAll();
    }

    @GetMapping({"/manage/{id}"})
    public Manage getManageById(@PathVariable Long id) {
        return (Manage)this.manageRepository.findById(id).orElseThrow(() -> {
            return new VenueNotFoundException(id);
        });
    }

    @PutMapping({"/manage/{id}"})
    public Manage updateManage(@RequestBody Manage newManage, @PathVariable Long id) {
        return (Manage)this.manageRepository.findById(id).map((manage) -> {
            manage.setBatch_id(newManage.getBatch_id());
            manage.setsName(newManage.getsName());
            manage.setsEmail(newManage.getsEmail());
            manage.setsPhone(newManage.getsPhone());
            manage.setStatus(newManage.isStatus());
            manage.setRegistered(newManage.getRegistered());
            manage.setsId(newManage.getsId());
            return (Manage)this.manageRepository.save(manage);
        }).orElseThrow(() -> {
            return new VenueNotFoundException(id);
        });
    }

    @DeleteMapping({"/manage/{id}"})
    public String deleteManage(@PathVariable Long id) {
        if (!this.manageRepository.existsById(id)) {
            throw new VenueNotFoundException(id);
        } else {
            this.manageRepository.deleteById(id);
            return "Manage Student with id " + id + "has been deleted successfully.";
        }
    }
}
