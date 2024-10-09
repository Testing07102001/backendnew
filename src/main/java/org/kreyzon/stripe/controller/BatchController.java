package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.dto.*;
import org.kreyzon.stripe.entity.Batch;
import org.kreyzon.stripe.entity.BatchDay;
import org.kreyzon.stripe.entity.ModuleDetails;
import org.kreyzon.stripe.exception.VenueNotFoundException;
import org.kreyzon.stripe.repository.BatchRepository;
import org.kreyzon.stripe.repository.RegistrationAdvanceRepository;
import org.kreyzon.stripe.service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "https://impulz-lms.com")
public class BatchController {
    private final BatchRepository batchRepository;
    private final BatchService batchService;

    @Autowired
    private RegistrationAdvanceRepository registrationAdvanceRepository;
    @Autowired
    public BatchController(BatchRepository batchRepository, BatchService batchService) {
        this.batchRepository = batchRepository;
        this.batchService = batchService;
    }

    @PostMapping("/batch")
    public ResponseEntity<Batch> createBatch(@RequestBody Batch batch) {
        Batch savedBatch = batchService.createBatch(batch);
        return ResponseEntity.ok(savedBatch);
    }

    @GetMapping("/batch-summary")
    public ResponseEntity<List<BatchSummaryDTO>> getAllBatchSummaries() {
        List<Batch> batches = batchRepository.findAll();
        List<BatchSummaryDTO> batchDTOs = batches.stream()
                .map(batch -> {
                    // Ensure intakeId is fetched from Batch entity
                    String intakeId = batch.getIntakeId(); // Assuming intakeId is a field in Batch entity

                    // Calculate the current module for each batch

                    // Get the active student count for the current batch
                    long activeStudentCount = registrationAdvanceRepository.countActiveStudentsByBatchId(batch.getId());
                    int currentModule = calculateCurrentModule(batch);
                    return new BatchSummaryDTO(
                            batch.getId(),
                            batch.getType(),
                            batch.getCourse(),
                            batch.getBatchcode(),
                            batch.getVenue(),
                            batch.getModuleDetails(),
                            currentModule,
                            Collections.singletonList(intakeId), // Use intakeId from Batch entity
                            activeStudentCount // Include the active student count
                    );
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(batchDTOs);
    }
    private int calculateCurrentModule(Batch batch) {
        // Get the current module from the batch
        int currentModule = batch.getCurrentModule();

        // Use the current date as reference date
        LocalDate referenceDate =LocalDate.now();

        // Calculate the difference in months between the reference date and the current date
        long monthsBetween = ChronoUnit.MONTHS.between(LocalDate.now(), referenceDate);

        // Update the current module based on the difference in months
        if (monthsBetween > 0) {
            if ("advance_diploma".equals(batch.getType())) {
                currentModule += monthsBetween; // Each module is one month
            } else if ("diploma".equals(batch.getType())) {
                currentModule += (int) Math.ceil((double) monthsBetween / 2); // Each module is two months
            } else {
                throw new IllegalArgumentException("Invalid batch type");
            }

            // Reset current module to 1 if it reaches 6
            if (currentModule > 6) {
                currentModule = 1;
            }
        }
        return currentModule;
    }


    @GetMapping("/batches")
    public ResponseEntity<List<Batch>> getAllBatches() {
        List<Batch> batches = batchRepository.findAll();
        LocalDate currentDate = LocalDate.now();

        // Set the current module for each batch
        batches.forEach(batch -> {
            int currentModule = calculateCurrentModule(batch.getStartDate(), currentDate);
            batch.setCurrentModule(currentModule);  // Assuming Batch has a currentModule field
        });

        return ResponseEntity.ok(batches);
    }
    private int calculateCurrentModule(LocalDate batchStartDate, LocalDate givenDate) {
        long monthsBetween = ChronoUnit.MONTHS.between(
                batchStartDate.withDayOfMonth(1),
                givenDate.withDayOfMonth(1)
        );
        return (int) (((monthsBetween + 3) % 6) + 1);
    }


    @GetMapping("/batchesize")

    public Page<Batch> getAllBatches(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return batchRepository.findAll(pageable);
    }

    @GetMapping("/batches/search")
    public Page<Batch> searchBatches(
            @RequestParam(defaultValue = "") String batchcode,
            @RequestParam(defaultValue = "") String type, // New parameter
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        if (!batchcode.isEmpty() && !type.isEmpty()) {
            // Both batchcode and type are provided
            return batchRepository.findByBatchcodeContainingIgnoreCaseAndType(batchcode, type, pageable);
        } else if (!batchcode.isEmpty()) {
            // Only batchcode is provided
            return batchRepository.findByBatchcodeContainingIgnoreCase(batchcode, pageable);
        } else if (!type.isEmpty()) {
            // Only type is provided
            return batchRepository.findByType(type, pageable);
        } else {
            // Neither batchcode nor type is provided
            return batchRepository.findAll(pageable);
        }
    }


    @GetMapping("/batch/{id}")
    public Batch getBatchById(@PathVariable Long id) {
        return batchRepository.findById(id).orElseThrow(() -> new VenueNotFoundException(id));
    }



    @PutMapping("/batch/{id}")
    public ResponseEntity<Batch> updateBatch(@PathVariable Long id,@RequestBody Batch updatedBatch) {

        Batch updatedEntity = batchService.updateBatch(id,updatedBatch);
        if (updatedEntity != null) {
            return ResponseEntity.ok(updatedEntity);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/batch/{id}")
    public String deleteBatch(@PathVariable Long id) {
        if (!batchRepository.existsById(id)) {
            throw new VenueNotFoundException(id);
        } else {
            batchRepository.deleteById(id);
            return "Batch with id " + id + " has been deleted successfully.";
        }
    }

    @GetMapping("/batch/count")
    public Long getBatchCount() {
        return batchRepository.count();
    }


    @GetMapping("/batch/byTrainersAndAssessors")
    public ResponseEntity<?> getBatchesByTrainerIdOrAssessorId(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String searchCriteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (id == null || id.isEmpty()) {
            // Return a message indicating that ID should be provided
            return ResponseEntity.badRequest().body("Please provide an ID");
        }

        Pageable pageable = PageRequest.of(page, size);

        // Retrieve batches from the repository
        List<Batch> allBatches = batchRepository.findAll();

        // Filter batches based on the provided ID (either trainerId or assessorId)
        List<Batch> filteredBatches = allBatches.stream()
                .filter(batch -> batch.getTrainers().contains(id) || batch.getAssessors().contains(id))
                .collect(Collectors.toList());

        // Apply search criteria to the filtered batches
        if (searchCriteria != null && !searchCriteria.isEmpty()) {
            filteredBatches = filteredBatches.stream()
                    .filter(batch -> batch.getBatchcode().contains(searchCriteria))
                    .collect(Collectors.toList());
        }

        // Paginate the filtered batches
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredBatches.size());
        Page<Batch> pageResult = new PageImpl<>(filteredBatches.subList(start, end), pageable, filteredBatches.size());

        // Return the paginated result
        return ResponseEntity.ok(pageResult);
    }
    @GetMapping("/{id}/current-module")
    public ResponseEntity<ModuleResponseDTO> getCurrentModuleByBatchId(@PathVariable("id") Long batchId) {
        // Fetch the batch by ID
        Optional<Batch> optionalBatch = batchRepository.findById(batchId);

        if (optionalBatch.isEmpty()) {
            // Return 404 Not Found if batch is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Batch batch = optionalBatch.get();

        // Use current date to calculate the current module
        LocalDate givenDate = LocalDate.now();

        // Calculate current module
        int currentModule = calculateCurrentModule(batch.getStartDate(), givenDate);

        // Get the module name corresponding to the current module
        String moduleName = getModuleNameByCurrentModule(batch.getModuleDetails(), currentModule);

        // Create response DTO
        ModuleResponseDTO response = new ModuleResponseDTO(currentModule, moduleName);

        // Return response
        return ResponseEntity.ok(response);
    }

    private String getModuleNameByCurrentModule(List<ModuleDetails> moduleDetailsList, int currentModule) {
        if (moduleDetailsList == null || moduleDetailsList.isEmpty()) {
            return "Module details not available";
        }

        // Ensure the module number is within the list size range
        if (currentModule <= moduleDetailsList.size()) {
            ModuleDetails moduleDetails = moduleDetailsList.get(currentModule - 1);
            return moduleDetails.getModuleName();
        } else {
            return "Module name not found";
        }
    }



}
