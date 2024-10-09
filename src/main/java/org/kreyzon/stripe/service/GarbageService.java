package org.kreyzon.stripe.service;

import org.kreyzon.stripe.entity.RegistrationAdvance;
import org.kreyzon.stripe.repository.GarbageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GarbageService {
    private final GarbageRepository garbageRepository;

    public GarbageService(GarbageRepository garbageRepository) {
        this.garbageRepository = garbageRepository;
    }

    public String findStatusByNric(String nric) {
        // Implement logic to fetch status by NRIC from the garbage repository
        return garbageRepository.findStatusByNric(nric);
    }

    public List<RegistrationAdvance> findAllDataByNric(String nric) {
        // Implement logic to fetch all data by NRIC from the garbage repository
        return garbageRepository.findAllDataByNric(nric);
    }

}
