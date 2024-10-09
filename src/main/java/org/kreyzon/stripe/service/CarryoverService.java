package org.kreyzon.stripe.service;

import org.kreyzon.stripe.entity.RegistrationAdvance;
import org.kreyzon.stripe.repository.RegistrationAdvanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarryoverService {
    @Autowired
    private RegistrationAdvanceRepository registrationAdvanceRepository;

    @Scheduled(cron = "0 0 0 1 * ?")  // Example: runs at midnight on the first day of every month
    public void handleCarryover() {
        List<RegistrationAdvance> ongoingEnrollments = registrationAdvanceRepository.findOngoingEnrollments();
        for (RegistrationAdvance enrollment : ongoingEnrollments) {
            enrollment.setEndModulus(1);
            registrationAdvanceRepository.save(enrollment);
        }
    }
}
