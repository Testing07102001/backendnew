package org.kreyzon.stripe.controller.wsq;

import org.kreyzon.stripe.entity.wsq.WSQFacilitator;
import org.kreyzon.stripe.service.wsq.WSQFacilitatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/wsq/facilitators")
public class WSQFacilitatorController {
    @Autowired
    private WSQFacilitatorService facilitatorService;

    // Create or update a facilitator
    @PostMapping
    public ResponseEntity<WSQFacilitator> createFacilitator(@RequestBody WSQFacilitator facilitator) {
        WSQFacilitator savedFacilitator = facilitatorService.saveFacilitator(facilitator);
        return ResponseEntity.ok(savedFacilitator);
    }

    // Get a facilitator by ID
    @GetMapping("/{id}")
    public ResponseEntity<WSQFacilitator> getFacilitatorById(@PathVariable Long id) {
        Optional<WSQFacilitator> facilitator = facilitatorService.getFacilitatorById(id);
        return facilitator.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all facilitators
    @GetMapping
    public ResponseEntity<List<WSQFacilitator>> getAllFacilitators() {
        List<WSQFacilitator> facilitators = facilitatorService.getAllFacilitators();
        return ResponseEntity.ok(facilitators);
    }

    // Delete a facilitator
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        // Check if the category exists
        if (!facilitatorService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Category with ID " + id + " not found.");
        }

        // If it exists, proceed to delete it
        facilitatorService.deleteFacilitator(id);
        return ResponseEntity.ok("Category with ID " + id + " deleted successfully.");
    }
    @PutMapping("/{id}")
    public ResponseEntity<WSQFacilitator> updateFacilitator(
            @PathVariable Long id,
            @RequestBody WSQFacilitator facilitatorDetails) {

        WSQFacilitator updatedFacilitator = facilitatorService.updateFacilitator(id, facilitatorDetails);
        return ResponseEntity.ok(updatedFacilitator);
    }
}
