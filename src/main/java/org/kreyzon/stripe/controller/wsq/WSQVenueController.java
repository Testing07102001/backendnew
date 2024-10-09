package org.kreyzon.stripe.controller.wsq;

import org.kreyzon.stripe.dto.WSQ.WSQVenueDTO;
import org.kreyzon.stripe.entity.wsq.WSQVenue;
import org.kreyzon.stripe.service.wsq.WSQVenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/wsq/venues")
public class WSQVenueController {
    @Autowired
    private WSQVenueService wsqVenueService;

    // Create or update a venue
    @PostMapping
    public ResponseEntity<WSQVenue> createVenue(@RequestBody WSQVenue venue) {
        WSQVenue savedVenue = wsqVenueService.saveVenue(venue);
        return ResponseEntity.ok(savedVenue);
    }

    // Get a venue by ID
    @GetMapping("/{id}")
    public ResponseEntity<WSQVenue> getVenueById(@PathVariable Long id) {
        Optional<WSQVenue> venue = wsqVenueService.getVenueById(id);
        return venue.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all venues
    @GetMapping("/finalls")
    public List<WSQVenueDTO> getAllVenues() {
        return wsqVenueService.getAllVenues().stream()
                .map(venue -> new WSQVenueDTO(venue.getId(), venue.getRoomno()))
                .collect(Collectors.toList());
    }


    // Delete a venue
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        // Check if the category exists
        if (!wsqVenueService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Category with ID " + id + " not found.");
        }

        // If it exists, proceed to delete it
        wsqVenueService.deleteFacilitator(id);
        return ResponseEntity.ok("Category with ID " + id + " deleted successfully.");
    }
    @PutMapping("/{id}")
    public ResponseEntity<WSQVenue> updateVenue(
            @PathVariable Long id,
            @RequestBody WSQVenue venueDetails) {

        WSQVenue updatedVenue = wsqVenueService.updateVenue(id, venueDetails);
        return ResponseEntity.ok(updatedVenue);
    }
}
