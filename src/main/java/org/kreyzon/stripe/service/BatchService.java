package org.kreyzon.stripe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kreyzon.stripe.entity.Batch;
import org.kreyzon.stripe.entity.Categories;
import org.kreyzon.stripe.entity.RegistrationAdvance;
import org.kreyzon.stripe.entity.Student;
import org.kreyzon.stripe.repository.BatchRepository;
import org.kreyzon.stripe.repository.CategoriesRepository;
import org.kreyzon.stripe.repository.RegistrationAdvanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

public class BatchService {
    @Autowired
    private BatchRepository batchRepository;


    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private RegistrationAdvanceRepository registrationAdvanceRepository;

    public List<Student> getStudentsByBatchId(Long batchId) {
        List<RegistrationAdvance> registrations = registrationAdvanceRepository.findByBatchId(batchId);
        return registrations.stream()
                .map(RegistrationAdvance::getStudent)
                .collect(Collectors.toList());
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Batch convertJsonToBatch(String json) throws IOException {
        return objectMapper.readValue(json, Batch.class);
    }

    public Optional<Batch> getBatchById(Long batchId) {
        return batchRepository.findById(batchId);
    }
    public Batch createBatch(Batch batch) {
        // Fetch the Category entity by categoryId
        Long categoryId = batch.getCategory().getId(); // Assuming category is already set in batch
        Categories category = categoriesRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + categoryId));

        // Set the Category entity in the Batch
        batch.setCategory(category);

        int currentModule = batch.getCurrentModule();
        LocalDate firstModuleStartDate = calculateFirstModuleStartDate(currentModule, batch.getType());
        batch.setStartDate(firstModuleStartDate);

        return batchRepository.save(batch);
    }

    private LocalDate calculateFirstSundayOfMonth(LocalDate currentDate) {
        LocalDate firstDayOfMonth = currentDate.with(TemporalAdjusters.firstDayOfMonth());
        return firstDayOfMonth.with(TemporalAdjusters.firstInMonth(DayOfWeek.SUNDAY));
    }

    private LocalDate calculateFirstSaturdayOfMonth(LocalDate currentDate) {
        LocalDate firstDayOfMonth = currentDate.with(TemporalAdjusters.firstDayOfMonth());
        return firstDayOfMonth.with(TemporalAdjusters.firstInMonth(DayOfWeek.SATURDAY));
    }

    private LocalDate calculateFirstSaturdayOfMonth(LocalDate currentDate, boolean oddMonth) {
        LocalDate firstDayOfMonth = currentDate.with(TemporalAdjusters.firstDayOfMonth());
        int month = firstDayOfMonth.getMonthValue();

        if (oddMonth && month % 2 == 0) {
            firstDayOfMonth = firstDayOfMonth.plusMonths(1); // Move to next month if the current month is even
        }

        return firstDayOfMonth.with(TemporalAdjusters.firstInMonth(DayOfWeek.SATURDAY));
    }
    private LocalDate calculateFirstModuleStartDate(int currentModule, String type) {
        LocalDate currentDate = LocalDate.now();
        LocalDate firstDay;
        LocalDate startMonth;

        if (type.equals("advance_diploma")) {
            firstDay = calculateFirstSundayOfMonth(currentDate);
            int monthOffset = currentModule - 1;
            startMonth = currentDate.minusMonths(monthOffset).with(TemporalAdjusters.firstInMonth(DayOfWeek.SUNDAY));

        } else if (type.equals("diploma")) {
            firstDay = calculateFirstSaturdayOfMonth(currentDate, true);
            int monthOffset = (currentModule - 1) * 2;
            startMonth = currentDate.minusMonths(monthOffset);
            if (currentModule != 1 && startMonth.getMonthValue() % 2 == 0) {
                startMonth = startMonth.plusMonths(1);
            }
            startMonth = startMonth.minusMonths(2).with(TemporalAdjusters.firstInMonth(DayOfWeek.SATURDAY));

        } else if (type.equals("c")) {
            firstDay = calculateFirstSundayOfMonth(currentDate);
            startMonth = firstDay;
        } else {
            throw new IllegalArgumentException("Invalid batch type");
        }

        return (currentModule == 1) ? firstDay : startMonth;
    }

    public Batch findById(Long batchId) {
        Optional<Batch> optionalBatch = batchRepository.findById(batchId);
        return optionalBatch.orElse(null); // Return the Batch or null if not present
    }

    public int getCurrentModule(LocalDate batchStartDate, LocalDate givenDate) {
        // Calculate the number of complete months between the batch start date and the given date
        long monthsBetween = ChronoUnit.MONTHS.between(
                batchStartDate.withDayOfMonth(1),
                givenDate.withDayOfMonth(1)
        );

        // Calculate the current module (1 to 6) with cyclical behavior
        int currentModule = (int) (((monthsBetween + 3) % 6) + 1);

        // If currentModule is already greater than 6, adjust it to loop back to 1
        if (currentModule > 6) {
            currentModule = currentModule % 6;
        }

        return currentModule;
    }

    public Batch updateBatch(Long id, Batch updatedBatch) {
        return batchRepository.findById(id)
                .map(existingBatch -> {
                    // Update simple properties
                    existingBatch.setCourse(updatedBatch.getCourse());
                    existingBatch.setCourseImage(updatedBatch.getCourseImage());
                    // existingBatch.setCourseId(updatedBatch.getCourseId());
                    existingBatch.setBatchcode(updatedBatch.getBatchcode());
                    existingBatch.setOnlineclasslink(updatedBatch.getOnlineclasslink());
                    //  existingBatch.setAttendanceqrlink(updatedBatch.getAttendanceqrlink());
                    // existingBatch.setEsignrequired(updatedBatch.getEsignrequired());
                    // existingBatch.setThreshold(updatedBatch.getThreshold());
                    // existingBatch.setIntakesize(updatedBatch.getIntakesize());
                    existingBatch.setType(updatedBatch.getType());
                    existingBatch.setVenue(updatedBatch.getVenue());
                    // existingBatch.setStatus(updatedBatch.getStatus());
                    existingBatch.setAssessors(updatedBatch.getAssessors());
                    existingBatch.setTrainers(updatedBatch.getTrainers());
                    existingBatch.setCourseFee(updatedBatch.getCourseFee());
                    existingBatch.setRegistrationFee(updatedBatch.getRegistrationFee());
                    existingBatch.setModuleDetails(updatedBatch.getModuleDetails());
                    existingBatch.setAssignment(updatedBatch.isAssignment());
                    existingBatch.setLessonsPerWeek(updatedBatch.getLessonsPerWeek());
//existingBatch.setType(updatedBatch.getType());
                    // Update current_module property
                    existingBatch.setCurrentModule(updatedBatch.getCurrentModule());
                    // Update the category if it has changed
                    if (updatedBatch.getCategory() != null) {
                        Categories category = categoriesRepository.findById(updatedBatch.getCategory().getId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + updatedBatch.getCategory().getId()));
                        existingBatch.setCategory(category);
                    }

                    // Update the days collection
                    //  existingBatch.setDays(updatedBatch.getDays());

                    if (updatedBatch.getDays() != null) {
                        existingBatch.getDays().clear();
                        existingBatch.getDays().addAll(updatedBatch.getDays());
                    }

                    // Update type-specific properties
                    if ("advance_diploma".equals(existingBatch.getType())) {
                        LocalDate currentDate = LocalDate.now();
                        int currentModule = existingBatch.getCurrentModule();

                        if (currentModule >= 1 && currentModule <= 6) {
                            currentDate = currentDate.minusMonths(currentModule - 1).with(TemporalAdjusters.firstDayOfMonth());
                        } else {
                            throw new IllegalArgumentException("Invalid currentModule value for advance diploma");
                        }

                        LocalDate firstSundayOfMonth = currentDate.with(TemporalAdjusters.firstInMonth(DayOfWeek.SUNDAY));
                        existingBatch.setStartDate(firstSundayOfMonth);
                    } else if ("diploma".equals(existingBatch.getType())) {
                        LocalDate currentDate = LocalDate.now();
                        int currentModule = existingBatch.getCurrentModule() ; // Multiply currentModule by 2
                        int updatedCurrentModule =currentModule*2;
                        // Adjust the currentDate based on the currentModule value
                        if (currentModule >= 1 && currentModule <= 6) {

                            currentDate = currentDate.minusMonths(updatedCurrentModule - 1).with(TemporalAdjusters.firstDayOfMonth());
                        } else {
                            throw new IllegalArgumentException("Invalid currentModule value for diploma");
                        }

                        LocalDate firstSaturdayOfMonth = currentDate.with(TemporalAdjusters.firstInMonth(DayOfWeek.SATURDAY));
                        existingBatch.setStartDate(firstSaturdayOfMonth);
                    } else {
                        throw new IllegalArgumentException("Unsupported batch type: " + existingBatch.getType());
                    }



                    // Save the updated Batch entity
                    return batchRepository.save(existingBatch);
                })
                .orElse(null);
    }

}