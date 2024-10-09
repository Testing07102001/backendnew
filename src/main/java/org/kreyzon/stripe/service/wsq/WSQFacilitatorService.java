package org.kreyzon.stripe.service.wsq;

import org.kreyzon.stripe.entity.wsq.WSQFacilitator;
import org.kreyzon.stripe.repository.wsq.WSQFacilitatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WSQFacilitatorService {
    @Autowired
    private WSQFacilitatorRepository facilitatorRepository;

    // Create or Update a facilitator
    public WSQFacilitator saveFacilitator(WSQFacilitator facilitator) {
        return facilitatorRepository.save(facilitator);
    }

    // Get a facilitator by ID
    public Optional<WSQFacilitator> getFacilitatorById(Long id) {
        return facilitatorRepository.findById(id);
    }

    // Get all facilitators
    public List<WSQFacilitator> getAllFacilitators() {
        return facilitatorRepository.findAll();
    }

    // Delete a facilitator
    public void deleteFacilitator(Long id) {
        facilitatorRepository.deleteById(id);
    }
    public boolean existsById(Long id) {
        return  facilitatorRepository.existsById(id); // Check for existence
    }
    public WSQFacilitator updateFacilitator(Long id, WSQFacilitator facilitatorDetails) {
        WSQFacilitator facilitator = facilitatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facilitator not found with id: " + id));

        facilitator.setSalutation(facilitatorDetails.getSalutation());
        facilitator.setUsertype(facilitatorDetails.getUsertype());
        facilitator.setName(facilitatorDetails.getName());
        facilitator.setNric(facilitatorDetails.getNric());
        facilitator.setEmail(facilitatorDetails.getEmail());
        facilitator.setPhone(facilitatorDetails.getPhone());
        facilitator.setEducational_level(facilitatorDetails.getEducational_level());
        facilitator.setQualification(facilitatorDetails.getQualification());
        facilitator.setDomain(facilitatorDetails.getDomain());
        facilitator.setGender(facilitatorDetails.getGender());
        facilitator.setPassword(facilitatorDetails.getPassword());
        // Update any other fields as needed
        facilitator.setProoftype(facilitatorDetails.getProoftype());
        return facilitatorRepository.save(facilitator);
    }
}
