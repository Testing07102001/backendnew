package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.SMTP;
import org.kreyzon.stripe.exception.VenueNotFoundException;
import org.kreyzon.stripe.repository.SMTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://impulz-lms.com")
public class SMTPController {
    @Autowired
    private SMTPRepository smtpRepository;

    @PostMapping({"/smtp"})
    public SMTP createSMTP(@RequestBody SMTP newSMTP) {
        return smtpRepository.save(newSMTP);
    }

    @GetMapping({"/smtps"})
    public List<SMTP> getAllSMTP() {
        return smtpRepository.findAll();
    }

    @PutMapping({"/smtp/{id}"})
    public SMTP updateSMTP(@RequestBody SMTP updatedSMTP, @PathVariable Long id) {
        return smtpRepository.findById(id)
                .map(existingSMTP -> {
                    existingSMTP.setHost(updatedSMTP.getHost());
                    existingSMTP.setUsername(updatedSMTP.getUsername());
                    existingSMTP.setPassword(updatedSMTP.getPassword());
                    existingSMTP.setPort(updatedSMTP.getPort());
                    return smtpRepository.save(existingSMTP);
                })
                .orElseThrow(()->{
                    return new VenueNotFoundException(id);
                });
    }

    @DeleteMapping({"/smtp/{id}"})
    public void deleteSMTP(@PathVariable Long id){
        this.smtpRepository.deleteById(id);
    }


}
