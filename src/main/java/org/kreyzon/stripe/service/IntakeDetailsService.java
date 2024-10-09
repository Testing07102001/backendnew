package org.kreyzon.stripe.service;

import org.kreyzon.stripe.dto.IntakeDetailsDTO;
import org.kreyzon.stripe.entity.Intake.DateSession;
import org.kreyzon.stripe.entity.Intake.DaySession;
import org.kreyzon.stripe.entity.Intake.Week;
import org.kreyzon.stripe.entity.IntakeDetails;
import org.kreyzon.stripe.entity.ScheduleDateDetails;
import org.kreyzon.stripe.entity.Session;
import org.kreyzon.stripe.repository.IntakeDetailsRepository;
import org.kreyzon.stripe.repository.ScheduleDateDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IntakeDetailsService {

    private final IntakeDetailsRepository intakeDetailsRepository;
    @Autowired
    private ScheduleDateDetailsRepository scheduleDateDetailsRepository;
    @Autowired
    public IntakeDetailsService(IntakeDetailsRepository intakeDetailsRepository) {
        this.intakeDetailsRepository = intakeDetailsRepository;
    }

    public Optional<IntakeDetails> getIntakeDetailsById(Long id) {
        return intakeDetailsRepository.findById(id);
    }

    public List<IntakeDetails> getAllIntakeDetails() {
        return intakeDetailsRepository.findAll();
    }

    // Method to get intake details by batchId
    public List<IntakeDetailsDTO> getIntakeDetailsByBatchId(Long batchId) {
        List<IntakeDetails> intakeDetailsList = intakeDetailsRepository.findByBatchId(batchId);
        return intakeDetailsList.stream()
                .map(IntakeDetailsMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<IntakeDetails> getFullIntakeDetailsByBatchIdAndIntakeId(String intakeId, Long batchId) {
        return intakeDetailsRepository.findByIntakeIdAndBatchId(intakeId, batchId);
    }


    public IntakeDetails updateIntakeDetails(Long id, IntakeDetails intakeDetails) {
        if (intakeDetailsRepository.existsById(id)) {
            intakeDetails.setId(id);
            return intakeDetailsRepository.save(intakeDetails);
        } else {
            throw new EntityNotFoundException("IntakeDetails not found with id: " + id);
        }
    }

    public Optional<IntakeDetails> getIntakeDetailsByIntakeId(String intakeId) {
        return intakeDetailsRepository.findByIntakeId(intakeId);
    }

    public void updateDateSessionAndSession(Long intakeDetailsId, Long scheduleId, Long weekId, Long dateSessionId, String day, LocalDate date, String sessionName, String startTime, String endTime) {
        IntakeDetails intakeDetails = intakeDetailsRepository.findById(intakeDetailsId)
                .orElseThrow(() -> new EntityNotFoundException("IntakeDetails not found"));

        ScheduleDateDetails scheduleDateDetails = intakeDetails.getScheduleDateDetails().stream()
                .filter(sdd -> sdd.getId().equals(scheduleId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("ScheduleDateDetails not found"));

        Week week = scheduleDateDetails.getWeeks().stream()
                .filter(w -> w.getId().equals(weekId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Week not found"));

        DateSession dateSession = week.getDates().stream()
                .filter(ds -> ds.getId().equals(dateSessionId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("DateSession not found"));

        // Update the day and date in DateSession
        dateSession.setDay(day);
        dateSession.setDate(date);

        // Find and update the specific session by sessionName
        if (dateSession.getSessions().containsKey(sessionName)) {
            Session session = dateSession.getSessions().get(sessionName);
            session.setStartTime(startTime);
            session.setEndTime(endTime);
        } else {
            throw new EntityNotFoundException("Session not found");
        }

        // Save the updated IntakeDetails entity
        intakeDetailsRepository.save(intakeDetails);
    }


    public boolean updateTrainer(Long intakeDetailsId, Long scheduleDateId, String newTrainer) {
        Optional<IntakeDetails> intakeDetailsOpt = intakeDetailsRepository.findById(intakeDetailsId);

        if (intakeDetailsOpt.isPresent()) {
            IntakeDetails intakeDetails = intakeDetailsOpt.get();
            List<ScheduleDateDetails> scheduleDateDetailsList = intakeDetails.getScheduleDateDetails();

            for (ScheduleDateDetails scheduleDateDetails : scheduleDateDetailsList) {
                if (scheduleDateDetails.getId().equals(scheduleDateId)) {
                    // Store only the trainer name part
                    scheduleDateDetails.setTrainer(newTrainer);
                    intakeDetailsRepository.save(intakeDetails);
                    return true;
                }
            }
        }
        return false;
    }
    public void updateDateSessionAndSession(Long scheduleId, Long weekId, Long dateSessionId, String day, LocalDate date, String sessionName, String startTime, String endTime) {
        ScheduleDateDetails scheduleDateDetails = scheduleDateDetailsRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("ScheduleDateDetails not found"));

        Week week = scheduleDateDetails.getWeeks().stream()
                .filter(w -> w.getId().equals(weekId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Week not found"));

        DateSession dateSession = week.getDates().stream()
                .filter(ds -> ds.getId().equals(dateSessionId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("DateSession not found"));

        // Update the day and date in DateSession
        dateSession.setDay(day);
        dateSession.setDate(date);

        // Find and update the specific session by sessionName
        if (dateSession.getSessions().containsKey(sessionName)) {
            Session session = dateSession.getSessions().get(sessionName);
            session.setStartTime(startTime);
            session.setEndTime(endTime);
        } else {
            throw new EntityNotFoundException("Session not found");
        }

        // Save the updated ScheduleDateDetails
        scheduleDateDetailsRepository.save(scheduleDateDetails);
    }
}
