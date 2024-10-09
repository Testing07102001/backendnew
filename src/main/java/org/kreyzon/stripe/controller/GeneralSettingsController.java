package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.GeneralSettings;
import org.kreyzon.stripe.exception.VenueNotFoundException;
import org.kreyzon.stripe.repository.GeneralSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "https://impulz-lms.com")
public class GeneralSettingsController {
    @Autowired
    private GeneralSettingsRepository generalSettingsRepository;

    @PostMapping({"/test"})
    public ResponseEntity<GeneralSettings> createUser(@RequestBody GeneralSettings user) {
        try {
            // Save the user object to the repository
            generalSettingsRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping({"/tests"})
    public ResponseEntity<List<GeneralSettings>> getAllUsers() {
        List<GeneralSettings> users = generalSettingsRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/test/{id}")
    public ResponseEntity<GeneralSettings> getUserById(@PathVariable Long id) {
        Optional<GeneralSettings> optionalUser = generalSettingsRepository.findById(id);
        return optionalUser.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }



    @PutMapping("/test/{id}")
    public ResponseEntity<GeneralSettings> updateGeneralSettings(
            @RequestBody GeneralSettings newGeneralSettings, @PathVariable Long id){
        GeneralSettings generalSettings = generalSettingsRepository.findById(id)
                .map(existingGeneralSettings ->{
                    existingGeneralSettings.setPassword(newGeneralSettings.getPassword());
                    existingGeneralSettings.setClientID(newGeneralSettings.getClientID());
                    existingGeneralSettings.setOrganization(newGeneralSettings.getOrganization());
                    existingGeneralSettings.setSSGServer(newGeneralSettings.getSSGServer());
                    existingGeneralSettings.setPhone(newGeneralSettings.getPhone());
                    existingGeneralSettings.setCompanyName(newGeneralSettings.getCompanyName());
                    existingGeneralSettings.setEmail(newGeneralSettings.getEmail());
                    existingGeneralSettings.setName(newGeneralSettings.getName());
                    existingGeneralSettings.setClientSecret(newGeneralSettings.getClientSecret());
                    existingGeneralSettings.setUrl(newGeneralSettings.getUrl());
                    return generalSettingsRepository.save(existingGeneralSettings);
                })
                .orElseThrow(()-> new VenueNotFoundException(id));
        return ResponseEntity.ok(generalSettings);
    }



    @DeleteMapping("/test/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Optional<GeneralSettings> optionalUser = generalSettingsRepository.findById(id);
        if (optionalUser.isPresent()) {
            generalSettingsRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
