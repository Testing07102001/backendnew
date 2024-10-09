package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.Venue;
import org.kreyzon.stripe.exception.VenueNotFoundException;
import org.kreyzon.stripe.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://impulz-lms.com")
public class VenueController {

    @Autowired
    private VenueRepository venueRepository;

    public VenueController() {
    }

    @PostMapping({"/venue"})
    Venue newVenue(@RequestBody Venue newVenue) {
        return (Venue)this.venueRepository.save(newVenue);
    }

    @GetMapping({"/venues"})
    List<Venue> getAllVenues() {
        return this.venueRepository.findAll();
    }

    @GetMapping({"/venue/{id}"})
    Venue getVenuebyId(@PathVariable Long id) {
        return (Venue)this.venueRepository.findById(id).orElseThrow(() -> {
            return new VenueNotFoundException(id);
        });
    }

    @PutMapping({"/venue/{id}"})
    Venue updateVenue(@RequestBody Venue newVenue, @PathVariable Long id) {
        return (Venue)this.venueRepository.findById(id).map((venue) -> {
            venue.setRoomno(newVenue.getRoomno());
            venue.setUnit(newVenue.getUnit());
            venue.setBlock(newVenue.getBlock());
            venue.setFloor(newVenue.getFloor());
            venue.setStreet(newVenue.getStreet());
            venue.setBuilding(newVenue.getBuilding());
            venue.setPostalcode(newVenue.getPostalcode());
            venue.setWheelchair(newVenue.getWheelchair());
            return (Venue)this.venueRepository.save(venue);
        }).orElseThrow(() -> {
            return new VenueNotFoundException(id);
        });
    }

    @DeleteMapping({"/venue/{id}"})
    String deleteVenue(@PathVariable Long id) {
        if (!this.venueRepository.existsById(id)) {
            throw new VenueNotFoundException(id);
        } else {
            this.venueRepository.deleteById(id);
            return "Venue with id " + id + " has  been deleted success.";
        }
    }
    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        return ResponseEntity.ok().allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE).build();
    }
}
