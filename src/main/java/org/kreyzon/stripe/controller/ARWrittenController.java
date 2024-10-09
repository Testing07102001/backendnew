package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.ARWritten;
import org.kreyzon.stripe.service.ARWrittenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/arwritten")
public class ARWrittenController {
    private final ARWrittenService aRWrittenService;

    @Autowired
    public ARWrittenController(ARWrittenService aRWrittenService) {
        this.aRWrittenService = aRWrittenService;
    }

    @GetMapping
    public List<ARWritten> getAllARWritten() {
        return aRWrittenService.getAllARWritten();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getARWrittenById(@PathVariable Long id) {
        ARWritten arWritten = aRWrittenService.getARWrittenById(id);
        if (arWritten != null) {
            return ResponseEntity.ok(arWritten);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ARWritten with ID " + id + " not found.");
        }
    }

    @PostMapping
    public ARWritten addARWritten(@RequestBody ARWritten aRWritten) {
        return aRWrittenService.saveARWritten(aRWritten);
    }

    @PutMapping("/{id}")
    public ARWritten updateARWritten(@PathVariable Long id, @RequestBody ARWritten updatedARWritten) {
        return aRWrittenService.updateARWritten(id, updatedARWritten);
    }

    @DeleteMapping("/{id}")
    public String deleteARWritten(@PathVariable Long id) {
        aRWrittenService.deleteARWritten(id);
        return "ARWritten with ID " + id + " has been deleted successfully.";
    }

    @GetMapping("/search")
    public ResponseEntity<?> getARWrittenUrlByParameters(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long batchId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long templateId) {

        if (studentId == null && batchId == null && courseId == null && templateId == null) {
            return ResponseEntity.badRequest().body("At least one parameter should be provided.");
        }

        String url = aRWrittenService.findUrlByParameters(studentId, batchId, courseId, templateId);

        if (url != null) {
            return ResponseEntity.ok(url);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No ARWritten found with the provided parameters.");
        }
    }
}
