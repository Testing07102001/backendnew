package org.kreyzon.stripe.controller.wsq;

import org.kreyzon.stripe.dto.WSQ.WSQBatchRequestDTO;
import org.kreyzon.stripe.entity.wsq.WSQBatch;
import org.kreyzon.stripe.service.wsq.WSQBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wsq/batches")
public class WSQBatchController {

    @Autowired
    private WSQBatchService batchService;

    @PostMapping
    public ResponseEntity<WSQBatch> createBatch(@RequestBody WSQBatchRequestDTO batchRequestDTO) {
        WSQBatch createdBatch = batchService.createBatch(
                batchRequestDTO.getCourseId(),
                batchRequestDTO.getVenueId(),
                batchRequestDTO
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBatch);
    }
    @GetMapping("/{batchId}")
    public ResponseEntity<WSQBatch> getBatchById(@PathVariable Long batchId) {
        WSQBatch batch = batchService.getBatchById(batchId);
        return ResponseEntity.ok(batch);
    }
    @GetMapping("/all")
    public ResponseEntity<List<WSQBatch>> getAllBatches() {
        List<WSQBatch> batches = batchService.getAllBatches();
        return ResponseEntity.ok(batches);
    }
//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
//        // Check if the category exists
//        if (!batchService.existsById(id)) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body("Category with ID " + id + " not found.");
//        }
//
//        // If it exists, proceed to delete it
//        batchService.deleteFacilitator(id);
//        return ResponseEntity.ok("Category with ID " + id + " deleted successfully.");
//    }
//    @PutMapping("/update/{batchId}")
//    public ResponseEntity<WSQBatch> updateBatch(
//            @PathVariable Long batchId,
//            @RequestBody WSQBatchRequestDTO batchRequestDTO) {
//
//        WSQBatch updatedBatch = batchService.updateBatch(
//                batchId,
//                batchRequestDTO.getCourseId(),
//                batchRequestDTO.getFacilitatorId(),
//                batchRequestDTO.getAssessorId(),
//                batchRequestDTO.getVenueId(),
//                batchRequestDTO
//        );
//
//        return ResponseEntity.ok(updatedBatch);
//    }
//@PutMapping("/{batchId}") // Endpoint to update a batch
//public ResponseEntity<WSQBatch> updateBatch(
//        @PathVariable Long batchId,
//        @RequestBody WSQBatchRequestDTO batchRequestDTO) {
//    try {
//        WSQBatch updatedBatch = batchService.updateBatch(batchId, batchRequestDTO);
//        return ResponseEntity.ok(updatedBatch);
//    } catch (RuntimeException e) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//    }
//}
}
