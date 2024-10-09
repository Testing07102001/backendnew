package org.kreyzon.stripe.service.wsq;

import org.kreyzon.stripe.entity.wsq.WSQVenue;
import org.kreyzon.stripe.repository.wsq.WSQVenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class WSQVenueService {
    @Autowired
    private WSQVenueRepository venueRepository;

    // Create or Update a venue
    public WSQVenue saveVenue(WSQVenue venue) {
        return venueRepository.save(venue);
    }

    // Get a venue by ID
    public Optional<WSQVenue> getVenueById(Long id) {
        return venueRepository.findById(id);
    }

    // Get all venues
    public List<WSQVenue> getAllVenues() {
        return venueRepository.findAll();
    }

    // Delete a venue
    public void deleteFacilitator(Long id) {
        venueRepository.deleteById(id);
    }
    public boolean existsById(Long id) {
        return  venueRepository.existsById(id); // Check for existence
    }
    public WSQVenue updateVenue(Long id, WSQVenue venueDetails) {
        WSQVenue venue = venueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venue not found with id: " + id));

        venue.setRoomno(venueDetails.getRoomno());
        venue.setUnit(venueDetails.getUnit());
        venue.setBlock(venueDetails.getBlock());
        venue.setFloor(venueDetails.getFloor());
        venue.setStreet(venueDetails.getStreet());
        venue.setBuilding(venueDetails.getBuilding());
        venue.setPostalcode(venueDetails.getPostalcode());
        venue.setWheelchair(venueDetails.getWheelchair());

        return venueRepository.save(venue);
    }
}
