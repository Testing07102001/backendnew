package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.entity.Curriculum;
import org.kreyzon.stripe.exception.VenueNotFoundException;
import org.kreyzon.stripe.repository.CurriculumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/curriculum")
@CrossOrigin(origins = "https://impulz-lms.com")
public class CurriculumController {
    @Autowired
    private CurriculumRepository curriculumRepository;

    @PostMapping // Updated the endpoint to /api/curriculum
    public ResponseEntity<Curriculum> newCurriculum(@RequestBody Curriculum newCurriculum) {
        Curriculum curriculum = curriculumRepository.save(newCurriculum);
        return ResponseEntity.status(HttpStatus.CREATED).body(curriculum);
    }

    @GetMapping // Updated the endpoint to /api/curriculum
    public ResponseEntity<List<Curriculum>> getAllCurriculum() {
        List<Curriculum> curriculum = curriculumRepository.findAll();
        return ResponseEntity.ok(curriculum);
    }

    @PutMapping("/{id}") // Use PutMapping for updating
    public ResponseEntity<Curriculum> updateCurriculum(@RequestBody Curriculum newCurriculum, @PathVariable Long id) {
        Curriculum curriculum = curriculumRepository.findById(id)
                .map(existingCurriculum -> {
                    existingCurriculum.setCourseid(newCurriculum.getCourseid());
                    existingCurriculum.setName(newCurriculum.getName());
                    existingCurriculum.setCreatedAt(newCurriculum.getCreatedAt());
                    return curriculumRepository.save(existingCurriculum);
                })
                .orElseThrow(() -> new VenueNotFoundException(id));

        return ResponseEntity.ok(curriculum);
    }

    @DeleteMapping("/{id}") // Updated the endpoint to /api/curriculum
    public ResponseEntity<String> deleteCurriculum(@PathVariable Long id) {
        if (!curriculumRepository.existsById(id)) {
            throw new VenueNotFoundException(id);
        } else {
            curriculumRepository.deleteById(id);
            return ResponseEntity.ok("Curriculum with " + id + " has been deleted Successfully");
        }
    }
}
