package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.dto.IntakeDetailsDTO;
import org.kreyzon.stripe.entity.IntakeDetails;
import org.kreyzon.stripe.service.IntakeDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/intake-details")
public class IntakeDetailsController {

    private final IntakeDetailsService intakeDetailsService;

    @Autowired
    public IntakeDetailsController(IntakeDetailsService intakeDetailsService) {
        this.intakeDetailsService = intakeDetailsService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<IntakeDetails> getIntakeDetails(@PathVariable Long id) {
        Optional<IntakeDetails> intakeDetails = intakeDetailsService.getIntakeDetailsById(id);
        return intakeDetails.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    @GetMapping("/intakeId/{intakeId}")
    public ResponseEntity<IntakeDetails> getIntakeDetailsByIntakeId(@PathVariable String intakeId) {
        Optional<IntakeDetails> intakeDetails = intakeDetailsService.getIntakeDetailsByIntakeId(intakeId);
        return intakeDetails.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{intakeDetailsId}/schedule/{scheduleId}/weeks/{weekId}/dateSessions/{dateSessionId}/sessions/{sessionName}")
    public ResponseEntity<Void> updateDateSessionAndSession(
            @PathVariable Long intakeDetailsId,
            @PathVariable Long scheduleId,
            @PathVariable Long weekId,
            @PathVariable Long dateSessionId,
            @RequestParam String day,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable String sessionName,
            @RequestParam String startTime,
            @RequestParam String endTime) {

        intakeDetailsService.updateDateSessionAndSession(intakeDetailsId, scheduleId, weekId, dateSessionId, day, date, sessionName, startTime, endTime);
        return ResponseEntity.ok().build();
    }


    @GetMapping
    public ResponseEntity<List<IntakeDetails>> getAllIntakeDetails() {
        List<IntakeDetails> intakeDetailsList = intakeDetailsService.getAllIntakeDetails();
        return ResponseEntity.ok(intakeDetailsList);
    }

    // API to get intake details by batchId
    @GetMapping("/batch/{batchId}")
    public ResponseEntity<List<IntakeDetailsDTO>> getIntakeDetailsByBatchId(@PathVariable Long batchId) {
        List<IntakeDetailsDTO> intakeDetailsDTOList = intakeDetailsService.getIntakeDetailsByBatchId(batchId);
        if (intakeDetailsDTOList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(intakeDetailsDTOList);
    }

    @GetMapping("/full/{intakeId}/batch/{batchId}")
    public ResponseEntity<List<IntakeDetails>> getFullIntakeDetailsByBatchIdAndIntakeId(
            @PathVariable String intakeId,
            @PathVariable Long batchId) {
        List<IntakeDetails> intakeDetailsList = intakeDetailsService.getFullIntakeDetailsByBatchIdAndIntakeId(intakeId, batchId);
        if (intakeDetailsList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(intakeDetailsList);
    }


    @PutMapping("/{id}")
    public ResponseEntity<IntakeDetails> updateIntakeDetails(@PathVariable Long id, @RequestBody IntakeDetails intakeDetails) {
        Optional<IntakeDetails> existingIntakeDetails = intakeDetailsService.getIntakeDetailsById(id);
        if (existingIntakeDetails.isPresent()) {
            // Update logic here
            IntakeDetails updatedIntakeDetails = intakeDetailsService.updateIntakeDetails(id, intakeDetails);
            return ResponseEntity.ok(updatedIntakeDetails);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}/schedule-date/{scheduleDateId}/trainer")
    public ResponseEntity<Void> updateTrainer(
            @PathVariable("id") Long intakeDetailsId,
            @PathVariable("scheduleDateId") Long scheduleDateId,
            @RequestBody Map<String, String> trainerData) {

        // Extract the trainer name value from the incoming map
        String newTrainer = trainerData.get("trainerName").trim();

        // Update trainer in service
        boolean updated = intakeDetailsService.updateTrainer(intakeDetailsId, scheduleDateId, newTrainer);

        if (updated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @PutMapping("/{scheduleId}/weeks/{weekId}/dateSessions/{dateSessionId}/sessions/{sessionName}")
    public ResponseEntity<Void> updateDateSessionAndSession(
            @PathVariable Long scheduleId,
            @PathVariable Long weekId,
            @PathVariable Long dateSessionId,
            @RequestParam String day,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable String sessionName,
            @RequestParam String startTime,
            @RequestParam String endTime) {

        intakeDetailsService.updateDateSessionAndSession(scheduleId, weekId, dateSessionId, day, date, sessionName, startTime, endTime);
        return ResponseEntity.ok().build();
    }

}
