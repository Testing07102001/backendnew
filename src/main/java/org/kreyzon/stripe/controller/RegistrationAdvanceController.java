package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.dto.*;
import org.kreyzon.stripe.entity.*;
import org.kreyzon.stripe.repository.IntakeCounterRepository;
import org.kreyzon.stripe.repository.RegistrationAdvanceRepository;
import org.kreyzon.stripe.repository.StudentRepository;
import org.kreyzon.stripe.repository.UserRepository;
import org.kreyzon.stripe.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/registrationadvances")
public class RegistrationAdvanceController {

    @Autowired
    private RegistrationAdvanceService registrationAdvanceService;
    @Autowired
    private StudentRepository studentRepository; // Add repository for Student
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StripeService stripeService;
@Autowired
private GarbageService garbageService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private RegistrationAdvanceRepository registrationAdvanceRepository;
    @Autowired
    private StudentService studentService;

    @Autowired
    private UserService userService;
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private IntakeCounterRepository intakeCounterRepository;


    @GetMapping("/getAllStudent")
    public ResponseEntity<List<RegistrationAdvance>> getAllEnrollments() {
        List<RegistrationAdvance> enrollments = registrationAdvanceService.getAllEnrollments();
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/student/{studentId}/registration-advances")
    public ResponseEntity<List<RegistrationAdvance>> getRegistrationAdvancesByStudentId(@PathVariable Long studentId) {
        List<RegistrationAdvance> registrationAdvances = registrationAdvanceService.getRegistrationAdvancesByStudentId(studentId);
        if (!registrationAdvances.isEmpty()) {
            return ResponseEntity.ok(registrationAdvances);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping
    public ResponseEntity<RegistrationAdvance> enrollStudent(@RequestBody EnrollmentDTO enrollmentDTO) {
        RegistrationAdvance enrollment = registrationAdvanceService.enrollStudent(
                enrollmentDTO,
                enrollmentDTO.getStartModulus(),
                enrollmentDTO.getEnrollmentDate()
        );
        if (enrollment != null) {
            return ResponseEntity.ok(enrollment);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/course-fee-detailsOffline")
    public ResponseEntity<RegistrationAdvance> addCourseFeeDetailsOffline(
            @PathVariable Long id,
            @RequestBody CourseFeeDetailsDTO feeDetailsDTO) {
        RegistrationAdvance updatedRegistrationAdvance = registrationAdvanceService.addCourseFeeDetailsOffline(id, feeDetailsDTO);
        return ResponseEntity.ok(updatedRegistrationAdvance);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistrationAdvance> getRegistrationAdvanceById(@PathVariable Long id) {
        RegistrationAdvance registrationAdvance = registrationAdvanceService.getRegistrationAdvanceById(id);
        if (registrationAdvance != null) {
            return new ResponseEntity<>(registrationAdvance, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/unapprovedPaymentType")
    public List<RegistrationAdvance> getUnapprovedRegistrationAdvancesPay() {
        return registrationAdvanceService.getUnapprovedRegistrationAdvancesPaymentType();
    }
    @PutMapping("/{id}")
    public ResponseEntity<RegistrationAdvance> updateRegistrationAdvance(@PathVariable Long id, @RequestBody RegistrationAdvance registrationAdvance) {
        RegistrationAdvance existingRegistrationAdvance = registrationAdvanceService.getRegistrationAdvanceById(id);
        if (existingRegistrationAdvance != null) {
            System.out.println("Edit Method");
            registrationAdvance.setId(id);
            registrationAdvance = registrationAdvanceService.saveRegistrationAdvance(registrationAdvance);
            return new ResponseEntity<>(registrationAdvance, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/session-details")
    public ResponseEntity<RegistrationAdvance> updateSessionDetails(
            @PathVariable Long id,
            @RequestBody SessionDetailsDTO sessionDetailsDTO) {
        RegistrationAdvance updatedRegistrationAdvance = registrationAdvanceService.updateSessionDetails(
                id,
                sessionDetailsDTO.getSessionId(),
                sessionDetailsDTO.getAmount(),
                sessionDetailsDTO.getSessionIdDate(),
                sessionDetailsDTO.getPaymentType()
        );

        if (updatedRegistrationAdvance != null) {
            return ResponseEntity.ok(updatedRegistrationAdvance);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<RegistrationAdvance> updateRegistrationAdvanceStatus(@PathVariable Long id) {
        RegistrationAdvance existingRegistrationAdvance = registrationAdvanceService.getRegistrationAdvanceById(id);
        if (existingRegistrationAdvance != null) {
            existingRegistrationAdvance.setPaymentStatus("paid");
            registrationAdvanceRepository.save(existingRegistrationAdvance);
            return new ResponseEntity<>(existingRegistrationAdvance, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistrationAdvance(@PathVariable Long id) {
        RegistrationAdvance existingRegistrationAdvance = registrationAdvanceService.getRegistrationAdvanceById(id);
        if (existingRegistrationAdvance != null) {
            registrationAdvanceService.deleteRegistrationAdvance(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private static final ZoneId IST_ZONE_ID = ZoneId.of("Asia/Singapore");



    @PostMapping("/{id}/approve")
    public ResponseEntity<RegistrationAdvance> approveRegistrationAdvance(@PathVariable Long id, Principal principal) {
        // Retrieve the RegistrationAdvance entity
        RegistrationAdvance existingRegistrationAdvance = registrationAdvanceService.getRegistrationAdvanceById(id);

        if (existingRegistrationAdvance != null) {
            // Retrieve the approver's name using the principal
            User approver = userService.findByEmail(principal.getName());

            if (approver != null) {
                // Set the approval status and approver's name
                existingRegistrationAdvance.setApprove("true");
                existingRegistrationAdvance.setApprover(approver.getName());
                existingRegistrationAdvance.setApproveDate(LocalDate.now());
                // Process ScheduleDates and set the isLate flag
                List<ScheduleDate> scheduleDates = existingRegistrationAdvance.getScheduleDate();
                LocalDate firstStartDate = null;

                if (!scheduleDates.isEmpty()) {
                    firstStartDate = scheduleDates.get(0).getStartDate(); // Use LocalDate directly
                }

                // Compare the current date with firstStartDate and set isLate flag
                if (firstStartDate != null) {
                    LocalDate currentDate = LocalDate.now();
                    boolean isLate = currentDate.isAfter(firstStartDate);
                    existingRegistrationAdvance.setLate(isLate); // Set to true if late, false otherwise
                } else {
                    existingRegistrationAdvance.setLate(false); // If no start date, assume not late
                }

                // Save the updated RegistrationAdvance entity
                registrationAdvanceService.saveRegistrationAdvance(existingRegistrationAdvance);

                // Send approval email
                sendApprovalEmail(existingRegistrationAdvance.getEmail(), existingRegistrationAdvance.getName());

                return new ResponseEntity<>(existingRegistrationAdvance, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private void sendApprovalEmail(String email, String name) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Approval Confirmation from Jurong Academy");

            String emailBody = "<div style='background-color: #f5f5f5; padding: 20px;'>" +
                    "<div style='background-color: white; padding: 20px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);'>" +
                    "<h2 style='color: #333;'>Approval Confirmation from Jurong Academy</h2>" +
                    "<p style='color: #666;'>Dear " + name + ",</p>" +
                    "<p style='color: #666;'>We are pleased to inform you that your application has been successfully approved.</p>" +
                    "<p style='color: #666;'>Thank you for choosing Jurong Academy.</p>" +
                    "</div>" +
                    "</div>" +
                    "<br/>" +
                    "<img src='https://ja.edu.sg/wp-content/uploads/2022/10/logo-new01-1.png' alt='Jurong Academy Logo' style='display: block; margin: 0 auto;' height='80px'>";

            helper.setText(emailBody, true);
            javaMailSender.send(message); // This line sends the email
        } catch (MessagingException e) {
            e.printStackTrace();
            // Handle email sending failure
        }
    }

    @PutMapping("/{id}/payment-status")
    public ResponseEntity<RegistrationAdvance> updatePaymentStatusAndSendReceipt(@PathVariable Long id) {
        RegistrationAdvance existingRegistrationAdvance = registrationAdvanceService.getRegistrationAdvanceById(id);
        if (existingRegistrationAdvance != null) {
            // Update payment status to "paid"
            existingRegistrationAdvance.setPaymentStatus("paid");

            // Save the updated registration advance
            RegistrationAdvance updatedRegistrationAdvance = registrationAdvanceService.saveRegistrationAdvance(existingRegistrationAdvance);

            // Generate invoice
            String invoice = generateInvoice(updatedRegistrationAdvance);

            // Send receipt to the user
            sendReceipt(updatedRegistrationAdvance.getEmail(), updatedRegistrationAdvance.getName(), invoice);

            return new ResponseEntity<>(updatedRegistrationAdvance, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private String generateInvoice(RegistrationAdvance registrationAdvance) {
        // Generate invoice using registrationAdvance details
        String invoice = "Invoice for " + registrationAdvance.getName() + " for course " + registrationAdvance.getBatch().getCourse() +
                " for the amount of $" + registrationAdvance.getAmount(); // You can adjust the invoice format as needed
        return invoice;
    }

    private void sendReceipt(String email, String name, String invoice) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Receipt for Your Payment");

            String emailBody = "<div style='background-color: #f5f5f5; padding: 20px;'>" +
                    "<div style='background-color: white; padding: 20px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);'>" +
                    "<h2 style='color: #333;'>Receipt for Your Payment</h2>" +
                    "<p style='color: #666;'>Dear " + name + ",</p>" +
                    "<p style='color: #666;'>Thank you for your payment. Please find attached the invoice for your reference:</p>" +
                    "<p style='color: #666;'>" + invoice + "</p>" +
                    "</div>" +
                    "</div>";

            helper.setText(emailBody, true);
            // Attach invoice file if necessary
            // helper.addAttachment("invoice.pdf", new ByteArrayResource(invoice.getBytes())); // Example for attaching a PDF invoice
            javaMailSender.send(message); // This line sends the email
        } catch (MessagingException e) {
            e.printStackTrace();
            // Handle email sending failure
        }
    }


    @GetMapping("/unapproved")
    public List<RegistrationAdvance> getUnapprovedRegistrationAdvances() {
        return registrationAdvanceService.getUnapprovedRegistrationAdvances();
    }

//    @PostMapping("/{id}/reject")
//    public ResponseEntity<RegistrationAdvance> rejectRegistrationAdvance(@PathVariable Long id) {
//        RegistrationAdvance existingRegistrationAdvance = registrationAdvanceService.getRegistrationAdvanceById(id);
//        if (existingRegistrationAdvance != null) {
//            existingRegistrationAdvance.setApprove("rejected"); // Set approve field to "rejected"
//            return new ResponseEntity<>(registrationAdvanceService.saveRegistrationAdvance(existingRegistrationAdvance), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }



    @GetMapping("/info")
    public List<RegistrationAdvanceInfo> getRegistrationAdvanceInfo() {
        List<RegistrationAdvance> registrationAdvances = registrationAdvanceService.getAllRegistrationAdvances();

        return registrationAdvances.stream()
                .map(registrationAdvance -> {
                    RegistrationAdvanceInfo info = new RegistrationAdvanceInfo();
                    info.setId(registrationAdvance.getId());
                    info.setName(registrationAdvance.getName());
               //     info.setSessionId(registrationAdvance.getSessionId());
               //     info.setSessionIdDate(registrationAdvance.getSessionIdDate());
                    info.setAddressHome(registrationAdvance.getAddressHome());
               //     info.setAmount(registrationAdvance.getAmount());
//                    info.setSessionInvoiceNo(registrationAdvance.getSessionInvoiceNo());
                    return info;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<RegistrationAdvanceInfo> getInfoAndSendEmail(@PathVariable Long id) {
            RegistrationAdvance  registrationAdvance = registrationAdvanceService.getRegistrationAdvanceById(id);

            if (registrationAdvance != null) {
                RegistrationAdvanceInfo info = registrationAdvanceService.populateRegistrationAdvanceInfo(registrationAdvance);
                return new ResponseEntity<RegistrationAdvanceInfo>(info, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

    //    @GetMapping("/info/session/{sessionId}")
//    public ResponseEntity<RegistrationAdvanceInfo> getRegistrationAdvanceInfoBySessionId(@PathVariable String sessionId) {
//        RegistrationAdvance registrationAdvance = registrationAdvanceService.getRegistrationAdvanceBySessionId(sessionId);
//        if (registrationAdvance != null) {
//            RegistrationAdvanceInfo info = new RegistrationAdvanceInfo();
//            info.setId(registrationAdvance.getId());
//            info.setName(registrationAdvance.getName());
//            info.setSureName(registrationAdvance.getSureName());
//      //      info.setSessionId(registrationAdvance.getSessionId());
//       //     info.setSessionIdDate(registrationAdvance.getSessionIdDate());
//            info.setAddressHome(registrationAdvance.getAddressHome());
//            info.setAmount(registrationAdvance.getAmount());
//            info.setDate(registrationAdvance.getDate());
//            info.setEmail(registrationAdvance.getEmail());
//            info.setDate(registrationAdvance.getDate());
//            info.setStudentId(registrationAdvance.getStudent().getId());
//
//            return new ResponseEntity<>(info, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
    // for fetch data in manage student
//@GetMapping("/batch/{batchId}/schedule-dates-with-student-info")
//public List<StudentScheduleDTO> getScheduleDatesWithStudentInfoByBatchId(@PathVariable Long batchId) {
//    return registrationAdvanceService.getApprovedScheduleDatesWithStudentInfoByBatchId(batchId);
//}


    @GetMapping("/{id}/details")
    public ResponseEntity<RegistrationAdvanceDetailsDTO> getRegistrationAdvanceDetailsById(@PathVariable Long id) {
        RegistrationAdvance registrationAdvance = registrationAdvanceService.getRegistrationAdvanceById(id);
        if (registrationAdvance != null) {
            // Convert List<ScheduleDate> to List<ScheduleDateDTO>
            List<ScheduleDateDTO> scheduleDateDTOList = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (ScheduleDate scheduleDate : registrationAdvance.getScheduleDate()) {
                ScheduleDateDTO scheduleDateDTO = new ScheduleDateDTO(
                        scheduleDate.getStartDate().format(formatter),
                        scheduleDate.getEndDate().format(formatter),
                        scheduleDate.getStartModulus()
                );
                scheduleDateDTOList.add(scheduleDateDTO);
            }

            // Populate the RegistrationAdvanceDetailsDTO
            RegistrationAdvanceDetailsDTO detailsDTO = new RegistrationAdvanceDetailsDTO(
                    registrationAdvance.getBatch().getCourse(),
                    registrationAdvance.getBatch().getVenue(),
                    registrationAdvance.getStudent().getName(),
                    registrationAdvance.getStudent().getNric(),
                    registrationAdvance.getBatch().getType(),
                    registrationAdvance.getDate()
            );
            detailsDTO.setScheduleDate(scheduleDateDTOList);

            return new ResponseEntity<>(detailsDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/registrationAdvancesByPaymentType")
    public ResponseEntity<List<RegistrationAdvance>> getRegistrationAdvancesByPaymentType(@RequestParam("paymentType") String paymentType) {
        // Retrieve registration advances by payment type and payment status
        List<RegistrationAdvance> registrationAdvances = registrationAdvanceRepository.findByPaymentTypeAndPaymentStatus(paymentType, "Pending");

        // Filter registration advances with paymentType "offline"
        registrationAdvances = registrationAdvances.stream()
                .filter(advance -> advance.getPaymentType().equals("offline"))
                .collect(Collectors.toList());

        if (registrationAdvances.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(registrationAdvances, HttpStatus.OK);
    }


    @PutMapping("/{id}/course-fee-details")
    public ResponseEntity<RegistrationAdvance> updateCourseFeeDetails(
            @PathVariable Long id,
            @RequestBody List<CourseFeeDetailsDTO> feeDetailsDTOs) {

        RegistrationAdvance updatedRegistrationAdvance = registrationAdvanceService.updateCourseFeeDetails(id, feeDetailsDTOs);
        return ResponseEntity.ok(updatedRegistrationAdvance);
    }
    @PostMapping("/{id}/course-fee-details")
    public ResponseEntity<RegistrationAdvance> addCourseFeeDetails(
            @PathVariable Long id,
            @RequestBody CourseFeeDetailsDTO feeDetailsDTO) {
        RegistrationAdvance updatedRegistrationAdvance = registrationAdvanceService.addCourseFeeDetails(id, feeDetailsDTO);
        return ResponseEntity.ok(updatedRegistrationAdvance);
    }
    @PostMapping("/{id}/course-fee-details/set-status")
    public ResponseEntity<RegistrationAdvance> addCourseFeeDetailsStatus(@PathVariable Long id) {
        RegistrationAdvance updatedRegistrationAdvance = registrationAdvanceService.addCourseFeeDetailsStatus(id);
        return ResponseEntity.ok(updatedRegistrationAdvance);
    }
    @GetMapping("/Students/alumni/Paid")
    public ResponseEntity<List<RegistrationAdvancesDetailsDTO>> getAllRegistrationsAndAlumni() {
        List<RegistrationAdvancesDetailsDTO> dtos1 = registrationAdvanceService.getAllRegistrationsAndAlumni();
        if (dtos1.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(dtos1);
        }
    }
    @GetMapping("pending/registrations/alumni")
    public ResponseEntity<List<RegistrationAdvancesDetailsDTO>> getsAllRegistrationsAndAlumni() {
        List<RegistrationAdvancesDetailsDTO> dtos = registrationAdvanceService.getsAllRegistrationsAndAlumni();
        if (dtos.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(dtos);
        }
    }

    @GetMapping("/unapprovedPaymentTypes")
    public List<RegistrationAdvance> getRegistrationAdvances(
            @RequestParam(value = "approveReference", required = false) String approveReference) {
        return registrationAdvanceService.getRegistrationAdvances(approveReference);
    }
    @GetMapping("/unapprovedPaymentTypesForNewOrRegister")
    public List<RegistrationAdvance> getRegistrationAdvancesForNewOrRegister(
            @RequestParam(value = "approveReference", required = false) String approveReference) {
        return registrationAdvanceService.getRegistrationAdvancesForNewOrRegister(approveReference);
    }

    @GetMapping("/registration-advances/{batchId}")
    public ResponseEntity<List<RegistrationAdvance>> getAllRegistrationAdvancesByBatchId(@PathVariable Long batchId) {
        List<RegistrationAdvance> registrationAdvances = registrationAdvanceService.getAllRegistrationAdvancesByBatchId(batchId);
        return ResponseEntity.ok().body(registrationAdvances);
    }
    @PostMapping("/send-receipts/{id}")
    public ResponseEntity<?> sendPaymentReceipts(@PathVariable("id") Long id) {
        try {
            registrationAdvanceService.sendPaymentReceipts(id);
            return ResponseEntity.ok("Payment receipt sent successfully.");
        } catch (MessagingException | IOException | URISyntaxException | InterruptedException e) {
            return ResponseEntity.status(500).body("Failed to send payment receipt: " + e.getMessage());
        }
    }
    @PostMapping("/send-receipt/{id}")
    public ResponseEntity<?> sendPaymentReceipt(@PathVariable("id") Long id) {
        try {
            registrationAdvanceService.sendPaymentReceiptEmail(id);
            return ResponseEntity.ok("Payment receipt sent successfully.");
        } catch (MessagingException | IOException e) {
            return ResponseEntity.status(500).body("Failed to send payment receipt: " + e.getMessage());
        } catch (URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/registration/{registrationId}/current-month-payment")
    public ResponseEntity<CurrentMonthPaymentDTO> getCurrentMonthPaymentDetails(@PathVariable Long registrationId) {
        Optional<RegistrationAdvance> optionalRegistrationAdvance = registrationAdvanceRepository.findById(registrationId);
        if (!optionalRegistrationAdvance.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CurrentMonthPaymentDTO());
        }

        RegistrationAdvance registrationAdvance = optionalRegistrationAdvance.get();

        // Populate RegistrationAdvanceInfo
        RegistrationAdvanceInfo info = registrationAdvanceService.populateRegistrationAdvanceInfo(registrationAdvance);

        // Get the course type and course fee
        String courseType = registrationAdvance.getType();
        double courseFee = registrationAdvance.getCourseFee();

        // Define the installment count and amount per installment based on course type
        int installmentCount;
        double installmentAmount;
        if ("advance_diploma".equalsIgnoreCase(courseType)) {
            installmentCount = 6;  // Advance Diploma has 6 installments
            installmentAmount = courseFee / installmentCount;
        } else if ("diploma".equalsIgnoreCase(courseType)) {
            installmentCount = 12;  // Diploma has 12 installments
            installmentAmount = courseFee / installmentCount;
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CurrentMonthPaymentDTO());
        }

        // Assuming startModuleStartDate is coming from scheduleDate
        LocalDate startModuleStartDate = null;
        if (registrationAdvance.getScheduleDate() != null && !registrationAdvance.getScheduleDate().isEmpty()) {
            ScheduleDate startModuleSchedule = registrationAdvance.getScheduleDate().get(0); // Get the first schedule date
            startModuleStartDate = startModuleSchedule.getStartDate(); // Assuming ScheduleDate has a getStartDate method
        }

        if (startModuleStartDate == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CurrentMonthPaymentDTO()); // Return error if startModuleStartDate is not present
        }

        LocalDate currentDate = LocalDate.of(2025, 8, 1);  // Set current date to May 1, 2025

        // Calculate the number of months between the start date and the current date
        long monthsSinceStartModule = ChronoUnit.MONTHS.between(
                YearMonth.from(startModuleStartDate),
                YearMonth.from(currentDate)
        );

        // Find the total paid amount for course fee
        double totalPaidForCourseFee = registrationAdvance.getCourseFeeDetails().stream()
                .filter(detail -> "CourseFee".equals(detail.getType()))
                .mapToDouble(CourseFeeDetails::getAmount)
                .sum();

        // Extract registration fee remaining from RegistrationAdvanceInfo
        long registrationFeeRemaining = info.getRegistrationFeeRemaining();

        // Calculate remaining course fee
        double remainingCourseFee = courseFee - totalPaidForCourseFee;

        // Calculate the total installments that should have been paid by now
        long installmentsDue = Math.min(monthsSinceStartModule + 1, installmentCount);

        // Calculate missed payments (if any)
        double totalDueForMissedMonths = installmentsDue * installmentAmount - totalPaidForCourseFee;

        // Ensure the total due doesn't exceed the remaining course fee
        double totalAmountDue = Math.min(totalDueForMissedMonths, remainingCourseFee);

        // Prepare the response DTO
        CurrentMonthPaymentDTO paymentDetails = new CurrentMonthPaymentDTO();
        paymentDetails.setAmountDueThisMonth((long) installmentAmount); // Only current month's installment
        paymentDetails.setPendingAmountForMissedMonths((long) ((long) (totalAmountDue - installmentAmount) + installmentAmount + registrationFeeRemaining)); // Missed months + remaining balance
        paymentDetails.setRemainingCourseFee((long) remainingCourseFee);
        paymentDetails.setTotalCourseFee((long) courseFee);
        paymentDetails.setTotalPaidSoFar((long) totalPaidForCourseFee);
        paymentDetails.setMessage("Amount due for the current and missed months, including remaining balance from previous payments.");

        return ResponseEntity.ok(paymentDetails);
    }


    @GetMapping("/module-fee-info/{id}")
    public ResponseEntity<ModuleFeeInfoDTO> getModuleFeeInfo(@PathVariable Long id) {
        // Fetch the RegistrationAdvance entity using the service
        RegistrationAdvance registrationAdvance = registrationAdvanceService.findById(id);

        if (registrationAdvance == null) {
            return ResponseEntity.notFound().build(); // Handle case where registrationAdvance is not found
        }

        // Create a new DTO object to hold the required information
        ModuleFeeInfoDTO moduleFeeInfo = new ModuleFeeInfoDTO();

        // Set start and end module dates
        if (registrationAdvance.getScheduleDate() != null && !registrationAdvance.getScheduleDate().isEmpty()) {
            ScheduleDate firstSchedule = registrationAdvance.getScheduleDate().get(0); // First schedule
            ScheduleDate lastSchedule = registrationAdvance.getScheduleDate().get(registrationAdvance.getScheduleDate().size() - 1); // Last schedule

            moduleFeeInfo.setStartModuleDate(firstSchedule.getStartDate());
            moduleFeeInfo.setEndModuleDate(lastSchedule.getEndDate());
        }

        // Set course and registration fee remaining values
        if (registrationAdvance.getCourseFeeDetails() != null && !registrationAdvance.getCourseFeeDetails().isEmpty()) {
            // Loop through course fee details to get the latest remaining balances
            registrationAdvance.getCourseFeeDetails().forEach(courseFeeDetail -> {
                if ("CourseFee".equalsIgnoreCase(courseFeeDetail.getType())) {
                    moduleFeeInfo.setCourseFeeRemaining(courseFeeDetail.getCourseFeeRemaining());
                } else if ("RegistrationFee".equalsIgnoreCase(courseFeeDetail.getType())) {
                    moduleFeeInfo.setRegistrationFeeRemaining(courseFeeDetail.getRegistrationFeeRemaining());
                }
            });
        }

        // Return the populated DTO
        return ResponseEntity.ok(moduleFeeInfo);
    }


    //    @DeleteMapping("/deleteregistrationalumni")
//    @Scheduled(cron = "0 0 8 * * ?")
//    public ResponseEntity<Void> deleteRegistrationAdvanceByAlumniStatus() {
//        registrationAdvanceService.deleteRegistrationAdvanceByAlumniStatus();
//        return ResponseEntity.ok().build();
//    }
@PostMapping("/alumni-to-garbage")
public ResponseEntity<String> transferAlumniToGarbage() {
    try {
        registrationAdvanceService.transferAlumniToGarbage();
        return ResponseEntity.ok("All eligible alumni registrations have been successfully transferred to garbage.");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during data transfer: " + e.getMessage());
    }
}
    @GetMapping("/garbage/intake/{intakeId}")
    public ResponseEntity<List<Garbage>> getGarbageByIntakeId(@PathVariable String intakeId) {
        List<Garbage> garbageRecords = registrationAdvanceService.getGarbageByIntakeId(intakeId);
        if (garbageRecords.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(garbageRecords);
        }
        return ResponseEntity.ok(garbageRecords);
    }
    @GetMapping("/alumni/{nric}")
    public ResponseEntity<?> getAlumniRegistrationAdvances(@PathVariable("nric") String nric) {
        // Step 1: Check in the garbage for alumni status by NRIC
        String garbageStatus = garbageService.findStatusByNric(nric);
        if ("Alumni".equalsIgnoreCase(garbageStatus)) {
            List<RegistrationAdvance> garbageAlumniData = garbageService.findAllDataByNric(nric);
            if (garbageAlumniData.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> garbageResponse = new HashMap<>();
            garbageResponse.put("status", "Alumni");
            garbageResponse.put("details", garbageAlumniData);

            return ResponseEntity.ok(garbageResponse);
        }

        // Step 2: Fetch the student status using the NRIC number from the main service
        String studentStatus = registrationAdvanceService.findStudentStatusByNric(nric);
        if (studentStatus == null) {
            // If the student status is not found, return a 404 Not Found response
            return ResponseEntity.notFound().build();
        }

        // Step 3: Check if the student status is "active", "new", or "register"
        if ("active".equalsIgnoreCase(studentStatus) || "new".equalsIgnoreCase(studentStatus) || "register".equalsIgnoreCase(studentStatus)) {
            // Fetch student details using the NRIC
            Optional<Student> studentOptional = registrationAdvanceService.findStudentByNric(nric);
            if (!studentOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Student student = studentOptional.get();
            // Fetch the RegistrationAdvance details
            List<RegistrationAdvance> registrationAdvances = registrationAdvanceService.findByStudentId(student.getId());

            // Prepare the response with the student ID, name, status, and registration advance ID if applicable
            Map<String, Object> response = new HashMap<>();
            response.put("studentId", student.getId());
            response.put("name", student.getName());
            response.put("status", studentStatus);

            if (!registrationAdvances.isEmpty()) {
                // Assuming you want to include only the first registrationAdvanceId, or adapt if needed
                response.put("registrationAdvanceId", registrationAdvances.get(0).getId());
            }

            return ResponseEntity.ok(response);
        }

        // Step 4: Check if the student status is not "Alumni"
        if (!"Alumni".equalsIgnoreCase(studentStatus)) {
            // If the student status is neither active, new, register, nor alumni, return the current status
            return ResponseEntity.ok("The student status is " + studentStatus + ".");
        }

        // Step 5: Fetch alumni registration advances by NRIC if the status is "Alumni"
        List<RegistrationAdvance> alumniRegistrationAdvances = registrationAdvanceService.findByNricAndCheckAlumniStatus(nric);
        if (alumniRegistrationAdvances.isEmpty()) {
            // If no alumni registration advances are found, return a 404 Not Found response
            return ResponseEntity.notFound().build();
        }

        // Step 6: Prepare the response with the status and details of the alumni registration advances
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Alumni");
        response.put("details", alumniRegistrationAdvances);

        return ResponseEntity.ok(response);
    }



    // change done here for  intakeid update and generate

    @PutMapping("/{batchId}/global-intake-counter")
    public ResponseEntity<Void> updateGlobalIntakeCounter(
            @PathVariable Long batchId,
            @RequestBody IntakeCounter intakeCounter) {

        try {
            registrationAdvanceService.updateGlobalIntakeCounter(batchId, intakeCounter);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // or handle as needed
        }
    }

    //changes done here for intakeId
    @GetMapping("/getallintakecounters")
    public List<IntakeCounter> getAllIntakeCounters(){
        return  intakeCounterRepository.findAll();

    }
    //changes done here for intakeId
    @GetMapping("/getintakecounter/{batchId}")
    public ResponseEntity<IntakeCounter> getIntakeCounterByBatchId(@PathVariable Long batchId) {
        Optional<IntakeCounter> intakeCounterOpt = intakeCounterRepository.findByBatchId(batchId);

        if (intakeCounterOpt.isPresent()) {
            return ResponseEntity.ok(intakeCounterOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/registrations/active/batch/{batchId}")
    public List<RegistrationAdvanceBatchDetailDTO> getActiveStudentsByBatchId(@PathVariable Long batchId) {
        return registrationAdvanceService.getActiveStudentsByBatchId(batchId); }


    @PostMapping("/sendInvoice")
    public ResponseEntity<String> sendInvoiceEmail(
            @RequestParam Long Id,
            @RequestParam String pdfUrl,
            @RequestParam String approver,
            @RequestParam String approverId)   {

        try {
            // Retrieve RegistrationAdvance entity
            Optional<RegistrationAdvance> optionalRegistrationAdvance = registrationAdvanceRepository.findById(Id);
            if (!optionalRegistrationAdvance.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("RegistrationAdvance not found");
            }
            RegistrationAdvance registrationAdvance = optionalRegistrationAdvance.get();
//            registrationAdvanceService.getRegistrationAdvanceInfoAndSendEmail(Id);
            // Retrieve the associated Student entity
            Student student = registrationAdvance.getStudent();
            if (student == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
            }

            // Retrieve the User entity by sfid (which corresponds to the Student ID)
            Optional<User> optionalUser = userRepository.findBySfidAndUsertype(student.getId(), 1);
            if (!optionalUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            User user = optionalUser.get();

            // Send email
            registrationAdvanceService.sendEmailforApproval(pdfUrl, registrationAdvance);

//            registrationAdvanceService. sendWhatsAppMessageForApprove(pdfUrl, registrationAdvance);
            // Update status of RegistrationAdvance, Student, and User
            registrationAdvance.setStatus("Register");
            student.setStatus("Register");
            user.setStatus("Register");

            // Set the email sent date

            // Set approver details
            registrationAdvance.setApprover(approver);
            registrationAdvance.setApproverId(approverId);

            // Process schedule dates
            List<ScheduleDate> scheduleDates = registrationAdvance.getScheduleDate();
            LocalDate firstStartDate = null;
            if (!scheduleDates.isEmpty()) {
                firstStartDate = scheduleDates.get(0).getStartDate(); // Use LocalDate directly
            }

            // Compare the current date with firstStartDate and set isLate flag
            if (firstStartDate != null) {
                LocalDate currentDate = LocalDate.now();
                boolean isLate = currentDate.isAfter(firstStartDate);
                registrationAdvance.setLate(isLate); // Set to true if late, false otherwise
            } else {
                registrationAdvance.setLate(false); // If no start date, assume not late
            }

            // Save the changes
            registrationAdvanceRepository.save(registrationAdvance);
            studentRepository.save(student);
            userRepository.save(user);

            // Include only the overall start date in the response
            return ResponseEntity.ok("Email sent, date stored, and status updated successfully. " +
                    "Overall start date: " + (firstStartDate != null ? firstStartDate.toString() : "No schedule date set") +
                    ". Is Late: " + (firstStartDate != null ? (LocalDate.now().isAfter(firstStartDate) ? "true" : "false") : "N/A"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email and update status");
        }
    }


    @GetMapping("/activeStudentDetails")
    public ResponseEntity<List<StudentDetailsDTOs>> getAllActiveStudentDetails() {
        List<StudentDetailsDTOs> studentDetailsList = registrationAdvanceService.getAllActiveStudentDetails();
        return ResponseEntity.ok(studentDetailsList);
    }

    @GetMapping("/activeStudentDetailss")
    public ResponseEntity<List<StudentDetailsDTO>> getAllActiveStudentDetailss() {
        List<StudentDetailsDTO> studentDetailsList = registrationAdvanceService.getAllActiveStudentDetailss();
        return ResponseEntity.ok(studentDetailsList);
    }

//    @GetMapping("/activeMark")
//    public ResponseEntity<List<MarkDTO>> getAllMark(){
//        List<MarkDTO> studentMark = registrationAdvanceService.getAllMarks();
//        return ResponseEntity.ok(studentMark);
//    }

    @GetMapping("/details/{id}")
    public ResponseEntity<PaymentDetailsDTO> getPaymentDetails(@PathVariable Long id) {
        PaymentDetailsDTO detailsDTO = registrationAdvanceService.getPaymentDetails(id);
        if (detailsDTO != null) {
            return ResponseEntity.ok(detailsDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/detailsForSendInvoice/{id}")
    public ResponseEntity<RegistrationAdvanceInfo> getRegistrationAdvanceDetails(@PathVariable Long id) {
        RegistrationAdvance registrationAdvance = registrationAdvanceService.getRegistrationAdvanceById(id);

        try {
            if (registrationAdvance != null) {
                // Check if the email has already been sent
                if (registrationAdvance.isEmailSent()) {
                    return new ResponseEntity<>(HttpStatus.OK);
                }

                // Send the email
                RegistrationAdvanceInfo info = registrationAdvanceService.getRegistrationAdvanceInfoAndSendEmail(id);

                if (info != null) {
                    return new ResponseEntity<>(info, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update-status")
    public ResponseEntity<String> updateStatus(@RequestBody StatusUpdateRequest request) {
        // Retrieve the RegistrationAdvance entity
        RegistrationAdvance registrationAdvance = registrationAdvanceRepository.findById(request.getRegistrationId())
                .orElseThrow(() -> new RuntimeException("RegistrationAdvance not found with id " + request.getRegistrationId()));
        registrationAdvance.setStatus(request.getStatus());
        // Update the status for the Student
        Student student = registrationAdvance.getStudent();
        if (student != null) {
            student.setStatus(request.getStatus());
            student.setRemarks(request.getRemarks());
            studentRepository.save(student);
        }

        // Update the status for the associated User entity using sfid
        Long sfid = registrationAdvance.getId();
        Optional<User> optionalUser = userRepository.findBySfidAndUsertype(sfid, 1);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No user found with sfid: " + sfid);
        }

        User user = optionalUser.get();
        user.setStatus(request.getStatus());
        userRepository.save(user);

        return ResponseEntity.ok("Status updated successfully");
    }
    @GetMapping("/pendingStudentDetails")
    public ResponseEntity<List<StudentDetailsDTO>> getAllPendingStudentDetails() {
        List<StudentDetailsDTO> studentDetailsList = registrationAdvanceService.getAllPendingStudentDetails();
        return ResponseEntity.ok(studentDetailsList);
    }

    @GetMapping("/by-year")
    public List<RegistrationAdvance> getAllByYear(@RequestParam("year") int year) {
        return registrationAdvanceService.getAllByYear(year);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<RegistrationAdvance> rejectRegistrationAdvance(@PathVariable Long id) {
        RegistrationAdvance existingRegistrationAdvance = registrationAdvanceService.getRegistrationAdvanceById(id);
        if (existingRegistrationAdvance != null) {
            existingRegistrationAdvance.setApprove("rejected"); // Set approve field to "rejected"
            existingRegistrationAdvance.setStatus("rejected");
            existingRegistrationAdvance.getStudent().setStatus("rejected");
            return new ResponseEntity<>(registrationAdvanceService.saveRegistrationAdvance(existingRegistrationAdvance), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/revertReject")
    public ResponseEntity<RegistrationAdvance> revertRejectRegistrationAdvance(@PathVariable Long id) {
        RegistrationAdvance existingRegistrationAdvance = registrationAdvanceService.getRegistrationAdvanceById(id);
        if (existingRegistrationAdvance != null) {
            // Retrieve the associated Student entity
            Student student = existingRegistrationAdvance.getStudent();

            if (student != null) {
                // Set the approve field to "rejected" in Student
//                student.setApprove("false");
                student.setStatus("new");
                // Save the updated Student entity
                studentService.saveStudent(student);

                // Update the RegistrationAdvance entity based on the updated Student
//                existingRegistrationAdvance.setApprove(student.getApprove());
                existingRegistrationAdvance.setStatus(student.getStatus());

                // Save the updated RegistrationAdvance entity
                RegistrationAdvance updatedRegistrationAdvance = registrationAdvanceService.saveRegistrationAdvance(existingRegistrationAdvance);
                return new ResponseEntity<>(updatedRegistrationAdvance, HttpStatus.OK);
            }

            // If no associated Student is found, return NOT_FOUND
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/registration/alumni")
    public ResponseEntity<List<RegistrationAdvance>> getActiveRegistrationsByIntakeIdalumni(@RequestParam String intakeId) {
        List<RegistrationAdvance> registrations = registrationAdvanceService.getActiveRegistrationsByIntakeIdalumni(intakeId);
        if (registrations.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if no results
        }
        return ResponseEntity.ok(registrations); // Return 200 OK with the list of registrations
    }

    @GetMapping("/registration/active")
    public ResponseEntity<List<RegistrationAdvance>> getActiveRegistrationsByIntakeId(@RequestParam String intakeId) {
        List<RegistrationAdvance> registrations = registrationAdvanceService.getActiveRegistrationsByIntakeId(intakeId);
        if (registrations.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if no results
        }
        return ResponseEntity.ok(registrations); // Return 200 OK with the list of registrations
    }



    @GetMapping("/students-by-intake")
    public List<StudentInfoDTO> getStudentsByIntakeId(@RequestParam String intakeId) {
        return registrationAdvanceService.getStudentsByIntakeId(intakeId);
    }


    @PostMapping("/{id}/moduleMarks")
    public ResponseEntity<RegistrationAdvanceResponseDTO> updateModuleMarks(
            @PathVariable Long id,
            @RequestBody List<ModuleMark> moduleMarks) {
        RegistrationAdvanceResponseDTO updatedRegistrationAdvance = registrationAdvanceService.updateModuleMarks(id, moduleMarks);

        if (updatedRegistrationAdvance == null) {
            return ResponseEntity.notFound().build(); // Return 404 Not Found
        }

        return ResponseEntity.ok(updatedRegistrationAdvance);
    }

    @PutMapping("/marks/{id}/moduleMarks1")
    public ResponseEntity<RegistrationAdvanceResponseDTO> updateModuleMarks1(
            @PathVariable Long id,
            @RequestBody List<ModuleMarksDTO> moduleMarksDTOs) {

        // Convert DTOs to ModuleMark entities
        List<ModuleMark> moduleMarks = moduleMarksDTOs.stream()
                .map(dto -> {
                    ModuleMark moduleMark = new ModuleMark();
                    moduleMark.setModuleName(dto.getModuleName());
                    moduleMark.setMark1(dto.getMark1());
                    moduleMark.setMark2(dto.getMark2());
                    moduleMark.setTotal(dto.getTotal());
                    return moduleMark;
                })
                .collect(Collectors.toList());

        try {
            RegistrationAdvanceResponseDTO updatedRegistrationAdvance = registrationAdvanceService.updateModuleMarks1(id, moduleMarks);

            if (updatedRegistrationAdvance == null) {
                return ResponseEntity.notFound().build(); // Return 404 Not Found
            }

            return ResponseEntity.ok(updatedRegistrationAdvance);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Return 400 Bad Request if validation fails
        }
    }
    @GetMapping("/{categoryId}/active-registrations")
    public ResponseEntity<List<RegistrationAdvance>> getActiveRegistrations(@PathVariable Long categoryId) {
        List<RegistrationAdvance> activeRegistrations = registrationAdvanceService.getActiveRegistrationsByCategoryId(categoryId);
        return ResponseEntity.ok(activeRegistrations);
    }

    @GetMapping("/intake/{intakeId}/students")
    public List<StudentDTO> getStudentsByBatchAndIntake(
                                                        @PathVariable String intakeId) {
        return registrationAdvanceService.getStudentsByBatchAndIntake(intakeId);
    }
    @GetMapping("/count/new")
    public long getCountNewRegistrations() {
        return registrationAdvanceService.countNewRegistrations();
    }
    @GetMapping("/status/new-or-register")
    public ResponseEntity<List<RegistrationAdvance>> getRegistrationsByStatus() {
        List<RegistrationAdvance> registrations = registrationAdvanceService.getRegistrationsByStatus();
        return ResponseEntity.ok(registrations);
    }

    @GetMapping("/api/students")
    public List<RegistrationAdvance> getStudentsByEnrollmentYearAndStatus(
            @RequestParam("startYear") int startYear,
            @RequestParam("endYear") int endYear) {

        return registrationAdvanceService.getStudentsByEnrollmentYearAndStatus(startYear, endYear);
    }

    @GetMapping("/{categoryId}/active-registrations/count")
    public ResponseEntity<Long> getActiveRegistrationsCount(@PathVariable Long categoryId) {
        long activeCount = registrationAdvanceService.countActiveRegistrationsByCategoryId(categoryId);
        return ResponseEntity.ok(activeCount);
    }
//    @GetMapping("/pending/{studentId}")
//    public ResponseEntity<?> getPendingInstallments(@PathVariable Long studentId) {
//        // Fetch the registration advance or course fee details for the student
//        Optional<RegistrationAdvance> registrationAdvanceOpt = registrationAdvanceRepository.findsByStudentId(studentId);
//
//        if (!registrationAdvanceOpt.isPresent()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found or no registration details available.");
//        }
//
//        RegistrationAdvance registrationAdvance = registrationAdvanceOpt.get();
//        List<CourseFeeDetails> courseFeeDetailsList = registrationAdvance.getCourseFeeDetails();
//        List<Map<String, Object>> pendingInstallmentsDetails = new ArrayList<>();
//
//        // Calculate pending installments
//        int totalInstallments;
//        if ("diploma".equalsIgnoreCase(registrationAdvance.getType())) {
//            totalInstallments = 12;
//        } else if ("advance_diploma".equalsIgnoreCase(registrationAdvance.getType())) {
//            totalInstallments = 6;
//        } else {
//            totalInstallments = 0;
//        }
//
//        LocalDate today = LocalDate.now(); // Get current date
//
//        // Iterate over course fee details and determine pending installments
//        for (CourseFeeDetails feeDetails : courseFeeDetailsList) {
//            // Check if the installment is unpaid or has a remaining balance
//            if ("Unpaid".equalsIgnoreCase(feeDetails.getStatus())
//                    || feeDetails.getRemainingBalance() > 0) {
//
//                // Collect installment details with remaining balance
//                Map<String, Object> installmentInfo = new HashMap<>();
//                installmentInfo.put("installmentCount", feeDetails.getInstallmentCount());
//                installmentInfo.put("remainingBalance", feeDetails.getRemainingBalance());
//                pendingInstallmentsDetails.add(installmentInfo);
//            }
//        }
//
//        // Calculate total remaining balance for all pending installments
//        long totalRemainingBalance = pendingInstallmentsDetails.stream()
//                .mapToLong(installment -> (Long) installment.get("remainingBalance"))
//                .sum();
//
//        // Return the response
//        Map<String, Object> response = new HashMap<>();
//        response.put("pendingInstallmentsCount", pendingInstallmentsDetails.size());
//        response.put("pendingInstallments", pendingInstallmentsDetails);
//        response.put("totalRemainingBalance", totalRemainingBalance);
//        response.put("message", pendingInstallmentsDetails.size() + " installments are pending up to today's date.");
//
//        return ResponseEntity.ok(response);
//    }


    @GetMapping("/installment-status/{registrationAdvanceId}")
    public ResponseEntity<InstallmentStatusDTO> getInstallmentStatus(@PathVariable Long registrationAdvanceId) {
        // Retrieve the RegistrationAdvance
        var registrationAdvance = registrationAdvanceRepository.findById(registrationAdvanceId);

        // Check if the resource exists
        if (registrationAdvance.isEmpty()) {
            // Return a 404 Not Found response if not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new InstallmentStatusDTO("RegistrationAdvance not found with ID: " + registrationAdvanceId));
        }

        // Extract CourseFeeDetails from the found RegistrationAdvance
        CourseFeeDetails courseFeeDetails = (CourseFeeDetails) registrationAdvance.get().getCourseFeeDetails();

        // Current date
        LocalDate currentDate = LocalDate.of(2024,12,30);

        // Calculate installment details
        List<InstallmentDetail> installmentDetails = courseFeeDetails.calculateInstallmentDetails(currentDate);

        // Prepare response DTO
        InstallmentStatusDTO status = new InstallmentStatusDTO();
        status.setInstallmentDetails(installmentDetails);

        return ResponseEntity.ok(status);
    }
}


