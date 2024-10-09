package org.kreyzon.stripe.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.kreyzon.stripe.dto.*;
import org.kreyzon.stripe.entity.*;
import org.kreyzon.stripe.entity.Intake.DateSession;
import org.kreyzon.stripe.entity.Intake.Week;
import org.kreyzon.stripe.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Service
public class RegistrationAdvanceService {

@Autowired
private AdminNotificationRepository adminNotificationRepository;
@Autowired
private TimeTableRepository timeTableRepository;
    @Autowired
    private RegistrationAdvanceRepository registrationAdvanceRepository;
    @Autowired
    private StudentIdRepository studentIdRepository;
    @Autowired
    private ModuleMarkRepository moduleMarkRepository;

@Autowired
private IntakeDetailsRepository  intakeDetailsRepository;
    @Autowired
    private  GarbageRepository garbageRepository;
    @Autowired
    IntakeCounterRepository intakeCounterRepository;
    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JavaMailSender mailSender;

    private static final String CLOUD_NAME = "dtprlsc0v";
    private static final String API_KEY = "992959977381914";
    private static final String API_SECRET = "458EcoRJkq-FKi6P-4du6gm45R0";

    private Object Kolkata;
    public String uploadPdfToCloudinaryAndGetUrl(byte[] pdfBytes, String publicId) throws IOException {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", CLOUD_NAME,
                "api_key", API_KEY ,
                "api_secret", API_SECRET
        ));
        Map uploadResult = cloudinary.uploader().upload(pdfBytes, ObjectUtils.asMap("public_id", publicId));
        return uploadResult.get("url").toString();
    }
    private Cloudinary cloudinary;
    public List<RegistrationAdvance> getAllRegistrationAdvances() {

        return registrationAdvanceRepository.findAll();
    }
    private static final ConcurrentHashMap<Long, AtomicLong> invoiceCounters = new ConcurrentHashMap<>();
    public RegistrationAdvance getRegistrationAdvanceById(Long id) {
        return registrationAdvanceRepository.findById(id).orElse(null);
    }



    public RegistrationAdvance saveRegistrationAdvance(RegistrationAdvance registrationAdvance) {
        if (registrationAdvance.getId() == null) {
            // New registration advance, set the date field
            registrationAdvance.setDate(getCurrentDateTime());
        }
   //     System.out.println("Entrolment Date :"+registrationAdvance.getEnrolledDate());
        List<ScheduleDate> objScheduler = calculateSubsequentModuliStartDatesStr(registrationAdvance.getEnrolledDate(),registrationAdvance.getStartModulus(),registrationAdvance.getBatch().getType());
        registrationAdvance.setScheduleDate(objScheduler);
        return registrationAdvanceRepository.save(registrationAdvance);
    }

    public void deleteRegistrationAdvance(Long id) {
        registrationAdvanceRepository.deleteById(id);
    }

    public List<RegistrationAdvance> getUnapprovedRegistrationAdvances() {
        return registrationAdvanceRepository.findByApprove("false");
    }

    private String getCurrentDateTime() {
        // Get current date and time in the desired format
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Singapore"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(formatter);
    }


    public List<RegistrationAdvance> getUnapprovedRegistrationAdvancesPaymentType() {
        return registrationAdvanceRepository.findByApproveAndPaymentTypeAndPaymentStatus("false","offline","pending");
    }

    public RegistrationAdvance enrollStudent(EnrollmentDTO enrollmentDTO, int startModulus, LocalDate enrollmentDate) {
        Optional<Batch> batchOpt = batchRepository.findById(enrollmentDTO.getBatchId());

        if (batchOpt.isPresent()) {
            Batch batch = batchOpt.get();

            // Create and save the new student
            Student student = new Student();
            student.setName(enrollmentDTO.getStudentName());
            student.setFullname(enrollmentDTO.getStudentName());
            student.setEmail(enrollmentDTO.getEmail());
            student.setNric(enrollmentDTO.getNric());
            student.setPhone(enrollmentDTO.getHandPhone());
            student.setGender(enrollmentDTO.getGender());
            student.setDob(enrollmentDTO.getDob());
            student.setType(enrollmentDTO.getType());
            student.setPassword(enrollmentDTO.getPassword());
            student.setDesignation(enrollmentDTO.getDesignation());
            student.setProfileUrl(enrollmentDTO.getProfilePicture());
            student.setApprove(enrollmentDTO.getApprove());// Set profileUrl here
            student.setStatus("new");




            student = studentRepository.save(student);

            // Create and save the new user
            User user = new User();
            user.setName(enrollmentDTO.getStudentName());
            user.setEmail(enrollmentDTO.getEmail());
            String encodedPassword = passwordEncoder.encode(enrollmentDTO.getPassword());
            user.setPassword(encodedPassword);
            String userId = UUID.randomUUID().toString();
            user.setUserId(userId);
            user.setStatus("new");
            user.setUsertype(1);
            user.setSfid(student.getId());
            userRepository.save(user);

            // Create the new enrollment
            RegistrationAdvance enrollment = new RegistrationAdvance();
            enrollment.setStudent(student);
            enrollment.setStatus("new");
            enrollment.setBatch(batch);
            enrollment.setEnrolledDate(enrollmentDTO.getEnrollmentDate());
            enrollment.setPaymentType(enrollmentDTO.getPaymentType());
            enrollment.setNric(enrollmentDTO.getNric());
//            enrollment.setStatus(student.getStatus());
            enrollment.setSequenceNumber(0);

            enrollment.setType(enrollmentDTO.getType());
            enrollment.setRegistrationFee(enrollment.getBatch().getRegistrationFee());
            enrollment.setCourseFee(enrollment.getBatch().getCourseFee());
            enrollment.setTitle(enrollmentDTO.getTitle());
            enrollment.setLate(false);
            enrollment.setIntake(enrollmentDTO.getIntake());
            enrollment.setApproveReference(enrollmentDTO.getApproveReference());
            enrollment.setProofType(enrollmentDTO.getProofType());
            enrollment.setStartModulus(enrollmentDTO.getStartModulus());
            enrollment.setName(enrollmentDTO.getSureName());
            enrollment.setCountryBirth(enrollmentDTO.getCountryBirth());
            enrollment.setDob(String.valueOf(enrollmentDTO.getDob()));
            enrollment.setGender(enrollmentDTO.getGender());
            enrollment.setNationality(enrollmentDTO.getNationality());
            enrollment.setEmergencyOtherDetails(enrollmentDTO.getEmergencyOtherDetails());
            enrollment.setPassportNo(enrollmentDTO.getPassportNo());
            enrollment.setDesignation(enrollmentDTO.getDesignation());
            enrollment.setAddressOffice(enrollmentDTO.getAddressOffice());
            enrollment.setAddressHome(enrollmentDTO.getAddressHome());
            enrollment.setPhoneOffice(enrollmentDTO.getPhoneOffice());
            enrollment.setPhoneHome(enrollmentDTO.getPhoneHome());
            enrollment.setHandPhone(enrollmentDTO.getHandPhone());
            enrollment.setEmail(enrollmentDTO.getEmail());
            enrollment.setEmergencyName(enrollmentDTO.getEmergencyName());
            enrollment.setEmergencyRelation(enrollmentDTO.getEmergencyRelation());
            enrollment.setEmergencyPhone(enrollmentDTO.getEmergencyPhone());
            enrollment.setKnowAboutProgram(enrollmentDTO.getKnowAboutProgram());
            enrollment.setApplicationSignature(enrollmentDTO.getApplicationSignature());
            LocalDate currentDate = LocalDate.now(ZoneId.of("Asia/Singapore"));
            enrollment.setDate(String.valueOf(currentDate));
            enrollment.setCompany(enrollmentDTO.getCompany());
            enrollment.setApprove(enrollmentDTO.getApprove());
             enrollment.setAmount(enrollmentDTO.getAmount());
            enrollment.setPaymentStatus(enrollmentDTO.getPaymentStatus());
            enrollment.setWorkPermitSpass(enrollmentDTO.getWorkPermitSpass());
            enrollment.setRepass(enrollmentDTO.getRepass());
            enrollment.setPaidDate(enrollmentDTO.getPaidDate());
            enrollment.setDependentPass(enrollmentDTO.getDependentPass());
            enrollment.setPassport(enrollmentDTO.getPassport());
            enrollment.setMarkSheet(enrollmentDTO.getMarkSheet());
            enrollment.setCertificate(enrollmentDTO.getCertificate());
            enrollment.setTranscript(enrollmentDTO.getTranscript());
            enrollment.setProfilePicture(enrollmentDTO.getProfilePicture());
            enrollment.setCompanyContact(enrollmentDTO.getCompanyContact());
            enrollment.setCompanyName(enrollment.getCompanyName());
            enrollment.setCompanyAddress(enrollmentDTO.getCompanyAddress());
            // Set institution details
            enrollment.setInstitutionDetails(enrollmentDTO.getInstitutionDetails());

            enrollment.setOtherDetails(enrollmentDTO.getOtherDetails());
            enrollment.setReferalfriend(enrollmentDTO.getReferalfriend());

            // Calculate and set dates
            LocalDate startDate = calculateEnrollmentStartDate(enrollmentDate, batch.getType(), startModulus);
            LocalDate endDate = calculateEnrollmentEndDate(startDate, batch.getType(), startModulus);
            enrollment.setStartModulus(startModulus);
            enrollment.setEnrolledDate(enrollmentDate);

            // Set end modulus based on batch type and start modulus
            int duration = batch.getType().equals("diploma") ? 12 : 6;
            int endModulus = (startModulus + duration - 1) % 6;

            // Automatically calculate start dates for subsequent moduli
            enrollment = calculateSubsequentModuliStartDates(enrollment, batch.getType(), startModulus);
            // Generate intakeId based on batch and enrollment date
//            generateIntakeId(enrollmentDTO, enrollmentDate);

//            generateIntakeId(enrollmentDTO, enrollmentDate);

//            // Set intakeId to enrollment

//            enrollment.setIntakeId(enrollmentDTO.getIntakeId());

            // Save enrollment
            return registrationAdvanceRepository.save(enrollment);



//            return enrollment;
        }
        return null;
    }

    //changes done here to reset and generate intakeId
    public void generateIntakeId(EnrollmentDTO enrollmentDTO, LocalDate enrollmentDate) {
        Long batchId = enrollmentDTO.getBatchId();
        if (batchId == null) {
            throw new IllegalArgumentException("Batch ID must be provided to generate intakeId.");
        }

        // Fetch the Batch entity from the repository
        Optional<Batch> batchOpt = batchRepository.findById(batchId);
        if (batchOpt.isEmpty()) {
            throw new IllegalArgumentException("Batch not found for ID: " + batchId);
        }
        Batch batch = batchOpt.get();

        int enrolledMonth = enrollmentDate.getMonthValue();
        int enrolledYear = enrollmentDate.getYear();

        // Fetch or create IntakeCounter entity
//        IntakeCounter intakeCounter = intakeCounterRepository.findById(1L).orElse(new IntakeCounter());
        Optional<IntakeCounter> intakeCounterOpt = intakeCounterRepository.findByBatchId(batchId);
        IntakeCounter intakeCounter;
        if (intakeCounterOpt.isPresent()) {
            intakeCounter = intakeCounterOpt.get();
        } else {
            // Create a new IntakeCounter if none exists for the batch
            intakeCounter = new IntakeCounter();
            intakeCounter.setBatchId(batchId);
            intakeCounter.setLastGeneratedMonth(0); // Initialize with defaults
            intakeCounter.setLastGeneratedYear(0);
            intakeCounter.setGlobalIntakeCounter(0);
            intakeCounter.setPreviousGlobalIntakeCounter(0);

            // Save the new IntakeCounter entity
            intakeCounterRepository.save(intakeCounter);
        }


        boolean isGapOfMonth = (enrolledYear != intakeCounter.getLastGeneratedYear()) ||
                (enrolledMonth - intakeCounter.getLastGeneratedMonth() > 1) ||
                (enrolledMonth - intakeCounter.getLastGeneratedMonth() < -10);

        if (intakeCounter.getLastGeneratedMonth() == 0||intakeCounter.getGlobalIntakeCounter() == 0) {
            // Reset counter for new month or year or if it's the first entity
            intakeCounter.setLastGeneratedMonth(enrolledMonth);
            intakeCounter.setLastGeneratedYear(enrolledYear);
            intakeCounter.setGlobalIntakeCounter(intakeCounter.getGlobalIntakeCounter()+1);
        }

        else if (enrolledMonth == intakeCounter.getLastGeneratedMonth() && enrolledYear == intakeCounter.getLastGeneratedYear()) {
            // Same month and year as before
            intakeCounter.setGlobalIntakeCounter(intakeCounter.getPreviousGlobalIntakeCounter());
        }
        else if (isGapOfMonth) {
            // Increment counter for new month or year
            intakeCounter.setLastGeneratedMonth(enrolledMonth);
            intakeCounter.setLastGeneratedYear(enrolledYear);
            intakeCounter.setGlobalIntakeCounter(intakeCounter.getPreviousGlobalIntakeCounter() + 1);
        } else {
            // Increment counter for new month or year
            intakeCounter.setLastGeneratedMonth(enrolledMonth);
            intakeCounter.setLastGeneratedYear(enrolledYear);
            intakeCounter.setGlobalIntakeCounter(intakeCounter.getPreviousGlobalIntakeCounter() + 1);
        }

        // Store current globalIntakeCounter as previousGlobalIntakeCounter for next comparison
        intakeCounter.setPreviousGlobalIntakeCounter(intakeCounter.getGlobalIntakeCounter());

        // Save/update IntakeCounter entity
        intakeCounterRepository.save(intakeCounter);

        // Fetch the last intake count for the given month and year (assuming this logic is implemented elsewhere)

        // Extract batchTypeCode, batchCourseCode, and enrolledYear
        String batchTypeCode = batch.getType();
        String batchCourseCode = batch.getCourse();


        String result = Arrays.stream(batchTypeCode.split("[\\s_]+"))                  // Split into words
                .filter(word -> !word.isEmpty())
                .map(word -> String.valueOf(word.charAt(0)))
                .collect(Collectors.joining())
                .toUpperCase();

        List<String> excludeWords = Arrays.asList("in", "to", "for","as","and","of","from","part-time","full-time","PartTime","FullTime");

//        String result1 = Arrays.stream(batchCourseCode.split("\\s+"))        // Split into words
//                .filter(word -> !word.isEmpty())
//                .filter(word -> !excludeWords.contains(word))
//                .map(word -> String.valueOf(word.charAt(0)))
//                .collect(Collectors.joining("."))
//                .replace("-", "")
//                .toUpperCase();
        String result1 = Arrays.stream(batchCourseCode.split("\\s+")) // Split into words
                .filter(word -> !word.isEmpty()) // Remove empty strings
                .filter(word -> !excludeWords.contains(word.toLowerCase())) // Filter out excluded words
                .map(word -> {
                    // If the word is a number, keep it as is
                    if (word.matches("\\d+")) {
                        return word;
                    }
                    // Otherwise, return the first letter
                    return String.valueOf(word.charAt(0));
                })
                .collect(Collectors.joining("")) // Join letters with dots
                .replace("-", "") // Remove hyphens
                .toUpperCase(); // Convert to uppercase

        // Remove the trailing period if present
        if (result1.endsWith(".")) {
            result1 = result1.substring(0, result1.length() - 1);
        }




        int enrolledYear1 = enrollmentDate.getYear() % 100;

        // Format the intakeId using the determined globalIntakeCounter
        String intakeId = String.format("%s-%02d%d", result1, enrolledYear1, intakeCounter.getGlobalIntakeCounter());

        // Set intakeId to the EnrollmentDTO
        enrollmentDTO.setIntakeId(intakeId);


// Set intakeId to the Batch entity
        batch.setIntakeId(intakeId);

// Save/update Batch entity
        batchRepository.save(batch);


    }
    public String generateIntakeIdDetails(EnrollmentDTO enrollmentDTO, LocalDate enrollmentDate) {
        Long batchId = enrollmentDTO.getBatchId();
        if (batchId == null) {
            throw new IllegalArgumentException("Batch ID must be provided to generate intakeId.");
        }

        // Fetch the Batch entity from the repository
        Optional<Batch> batchOpt = batchRepository.findById(batchId);
        if (batchOpt.isEmpty()) {
            throw new IllegalArgumentException("Batch not found for ID: " + batchId);
        }
        Batch batch = batchOpt.get();

        int enrolledMonth = enrollmentDate.getMonthValue();
        int enrolledYear = enrollmentDate.getYear();

        // Fetch or create IntakeCounter entity
//        IntakeCounter intakeCounter = intakeCounterRepository.findById(1L).orElse(new IntakeCounter());
        Optional<IntakeCounter> intakeCounterOpt = intakeCounterRepository.findByBatchId(batchId);
        IntakeCounter intakeCounter;
        if (intakeCounterOpt.isPresent()) {
            intakeCounter = intakeCounterOpt.get();
        } else {
            // Create a new IntakeCounter if none exists for the batch
            intakeCounter = new IntakeCounter();
            intakeCounter.setBatchId(batchId);
            intakeCounter.setLastGeneratedMonth(0); // Initialize with defaults
            intakeCounter.setLastGeneratedYear(0);
            intakeCounter.setGlobalIntakeCounter(0);
            intakeCounter.setPreviousGlobalIntakeCounter(0);

            // Save the new IntakeCounter entity
            intakeCounterRepository.save(intakeCounter);
        }


        boolean isGapOfMonth = (enrolledYear != intakeCounter.getLastGeneratedYear()) ||
                (enrolledMonth - intakeCounter.getLastGeneratedMonth() > 1) ||
                (enrolledMonth - intakeCounter.getLastGeneratedMonth() < -10);

        if (intakeCounter.getLastGeneratedMonth() == 0||intakeCounter.getGlobalIntakeCounter() == 0) {
            // Reset counter for new month or year or if it's the first entity
            intakeCounter.setLastGeneratedMonth(enrolledMonth);
            intakeCounter.setLastGeneratedYear(enrolledYear);
            intakeCounter.setGlobalIntakeCounter(intakeCounter.getGlobalIntakeCounter()+1);
        }

        else if (enrolledMonth == intakeCounter.getLastGeneratedMonth() && enrolledYear == intakeCounter.getLastGeneratedYear()) {
            // Same month and year as before
            intakeCounter.setGlobalIntakeCounter(intakeCounter.getPreviousGlobalIntakeCounter());
        }
        else if (isGapOfMonth) {
            // Increment counter for new month or year
            intakeCounter.setLastGeneratedMonth(enrolledMonth);
            intakeCounter.setLastGeneratedYear(enrolledYear);
            intakeCounter.setGlobalIntakeCounter(intakeCounter.getPreviousGlobalIntakeCounter() + 1);
        } else {
            // Increment counter for new month or year
            intakeCounter.setLastGeneratedMonth(enrolledMonth);
            intakeCounter.setLastGeneratedYear(enrolledYear);
            intakeCounter.setGlobalIntakeCounter(intakeCounter.getPreviousGlobalIntakeCounter() + 1);
        }

        // Store current globalIntakeCounter as previousGlobalIntakeCounter for next comparison
        intakeCounter.setPreviousGlobalIntakeCounter(intakeCounter.getGlobalIntakeCounter());

        // Save/update IntakeCounter entity
        intakeCounterRepository.save(intakeCounter);

        // Fetch the last intake count for the given month and year (assuming this logic is implemented elsewhere)

        // Extract batchTypeCode, batchCourseCode, and enrolledYear
        String batchTypeCode = batch.getType();
        String batchCourseCode = batch.getCourse();


        String result = Arrays.stream(batchTypeCode.split("[\\s_]+"))                  // Split into words
                .filter(word -> !word.isEmpty())
                .map(word -> String.valueOf(word.charAt(0)))
                .collect(Collectors.joining())
                .toUpperCase();

        List<String> excludeWords = Arrays.asList("in", "to", "for","as","and","of","from","part-time","full-time","PartTime","FullTime");

//        String result1 = Arrays.stream(batchCourseCode.split("\\s+"))        // Split into words
//                .filter(word -> !word.isEmpty())
//                .filter(word -> !excludeWords.contains(word))
//                .map(word -> String.valueOf(word.charAt(0)))
//                .collect(Collectors.joining("."))
//                .replace("-", "")
//                .toUpperCase();
        String result1 = Arrays.stream(batchCourseCode.split("\\s+")) // Split into words
                .filter(word -> !word.isEmpty()) // Remove empty strings
                .filter(word -> !excludeWords.contains(word.toLowerCase())) // Filter out excluded words
                .map(word -> {
                    // If the word is a number, keep it as is
//                    if (word.matches("\\d+")) {
//                        return word;
//                    }
//                    // Otherwise, return the first letter
//                    return String.valueOf(word.charAt(0));
                    if (word.equals("computer")) {
                        return "comp";
                    } else if (word.matches("\\d+")) {
                        return word;
                    } else {
                        // For any other non-numeric word, return the first letter
                        return String.valueOf(word.charAt(0));
                    }
                })
                .collect(Collectors.joining("")) // Join letters with dots
                .replace("-", "") // Remove hyphens
                .toUpperCase(); // Convert to uppercase

        // Remove the trailing period if present
        if (result1.endsWith(".")) {
            result1 = result1.substring(0, result1.length() - 1);
        }




        int enrolledYear1 = enrollmentDate.getYear() % 100;

        // Format the intakeId using the determined globalIntakeCounter
        String intakeId = String.format("%s-%02d%d", result1, enrolledYear1, intakeCounter.getGlobalIntakeCounter());

        // Set intakeId to the EnrollmentDTO
        enrollmentDTO.setIntakeId(intakeId);


// Set intakeId to the Batch entity
        batch.setIntakeId(intakeId);

// Save/update Batch entity
        batchRepository.save(batch);


        return intakeId;
    }

    //changes done here to reset and generate intakeId
    @Transactional
    public void updateGlobalIntakeCounter(Long batchId, IntakeCounter newIntakeCounter) {
        Optional<IntakeCounter> intakeCounterOpt = intakeCounterRepository.findByBatchId(batchId);
        if (intakeCounterOpt.isPresent()) {
            IntakeCounter intakeCounter = intakeCounterOpt.get();

            // Update intakeCounter with the new values
            intakeCounter.setGlobalIntakeCounter(newIntakeCounter.getGlobalIntakeCounter());
            intakeCounter.setLastGeneratedMonth(0);
            intakeCounter.setLastGeneratedYear(0);
            intakeCounter.setPreviousGlobalIntakeCounter(intakeCounter.getGlobalIntakeCounter());

            // Save the updated entity
            IntakeCounter updatedIntakeCounter = intakeCounterRepository.save(intakeCounter);

            // Ensure the updated entity is returned and used for further processing if needed
            // Example: return updatedIntakeCounter;
        } else {
            throw new IllegalArgumentException("IntakeCounter not found for Batch ID: " + batchId);
        }
    }

    private LocalDate calculateEnrollmentStartDate(LocalDate enrollmentDate, String batchType, int startModulus) {
        LocalDate startDate = enrollmentDate;

        if (batchType.equals("advance_diploma")) {
            // Advance diploma: each modulus starts on the first Sunday of the next month
            startDate = enrollmentDate.with(TemporalAdjusters.firstDayOfNextMonth())
                    .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        } else {
            // Diploma: Determine the start date based on the next odd month's start
            LocalDate firstOddMonth = enrollmentDate.plusMonths(1).with(TemporalAdjusters.firstDayOfMonth());

            // Ensure we start on an odd month
            if (firstOddMonth.getMonthValue() % 2 == 0) {
                firstOddMonth = firstOddMonth.plusMonths(1);
            }

            // If the first day of the odd month is Saturday or Sunday, use it directly
            if (firstOddMonth.getDayOfWeek() == DayOfWeek.SATURDAY ||
                    firstOddMonth.getDayOfWeek() == DayOfWeek.SUNDAY) {
                startDate = firstOddMonth;
            } else {
                // Otherwise, find the first Saturday and Sunday of the odd month
                LocalDate firstSaturday = firstOddMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
                LocalDate firstSunday = firstOddMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

                // Choose the earliest of the two dates
                startDate = (firstSaturday.isBefore(firstSunday)) ? firstSaturday : firstSunday;
            }
        }

        return startDate;
    }




    private LocalDate calculateEnrollmentEndDate(LocalDate startDate, String batchType,int startModulus) {
        LocalDate endDate = startDate;
        if (batchType.equals("advance_diploma")) {
            endDate = startDate.with(TemporalAdjusters.lastDayOfMonth())
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        } else {
            // Diploma: Determine the end date based on the next even month's end
            LocalDate nextEvenMonth = endDate.plusMonths(1).with(TemporalAdjusters.firstDayOfMonth());

            // Ensure we start on an even month
            if (nextEvenMonth.getMonthValue() % 2 != 0) {
                nextEvenMonth = nextEvenMonth.plusMonths(1);
            }

            // Check if the last day of the even month is Saturday or Sunday
            LocalDate lastDayOfEvenMonth = nextEvenMonth.with(TemporalAdjusters.lastDayOfMonth());
            if (lastDayOfEvenMonth.getDayOfWeek() == DayOfWeek.SATURDAY || lastDayOfEvenMonth.getDayOfWeek() == DayOfWeek.SUNDAY) {
                endDate = lastDayOfEvenMonth;
            } else {
                // Find the last Saturday and Sunday of the even month and choose the latest
                LocalDate lastSaturday = lastDayOfEvenMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY));
                LocalDate lastSunday = lastDayOfEvenMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
                endDate = (lastSaturday.isAfter(lastSunday)) ? lastSaturday : lastSunday;
            }
        }

        return endDate;
    }

    public List<RegistrationAdvance> getAllEnrollments() {
        return registrationAdvanceRepository.findAll();
    }

    private List<ScheduleDate> calculateSubsequentModuliStartDatesStr(LocalDate enrolledDate, int startModulus, String batchType) {
        List<ScheduleDate> objList = new ArrayList<>();

        try {
            LocalDate startDate = enrolledDate;
            int totalModuli = 6;
               for (int i = 0; i < totalModuli; i++) {
                int currentModulus = (startModulus + i - 1) % totalModuli + 1;

                startDate = calculateEnrollmentStartDate(startDate, batchType, currentModulus);
                LocalDate endDate = calculateEnrollmentEndDate(startDate, batchType, currentModulus);
                ScheduleDate objScheduleDate = new ScheduleDate();
                objScheduleDate.setStartDate(startDate);
                objScheduleDate.setEndDate(endDate);
                objScheduleDate.setStartModulus(currentModulus);
                // Add the modulus DTO to the list
                objList.add(objScheduleDate);
            }
        } catch (Exception e) {
            System.out.print("Error on calculation: " + e.getMessage());
        }
        return objList;
    }

    private RegistrationAdvance calculateSubsequentModuliStartDates(RegistrationAdvance enrollment, String batchType, int startModulus) {
          RegistrationAdvance nextEnrollment = new RegistrationAdvance();
            nextEnrollment.setStudent(enrollment.getStudent());
            nextEnrollment.setBatch(enrollment.getBatch());
            nextEnrollment.setEnrolledDate(enrollment.getEnrolledDate());
            nextEnrollment.setName(String.valueOf(enrollment.getStudent()));
            nextEnrollment.setNationality(enrollment.getNationality());
            nextEnrollment.setEmergencyOtherDetails(enrollment.getEmergencyOtherDetails());
            nextEnrollment.setHandPhone(enrollment.getHandPhone());
            nextEnrollment.setType(enrollment.getType());
            nextEnrollment.setTitle(enrollment.getTitle());
            nextEnrollment.setIntake(enrollment.getIntake());
            nextEnrollment.setRegistrationFee(enrollment.getRegistrationFee());
            nextEnrollment.setCourseFee(enrollment.getCourseFee());
            nextEnrollment.setProofType(enrollment.getProofType());
        nextEnrollment.setPaymentType(enrollment.getPaymentType());
        nextEnrollment.setStartModulus(enrollment.getStartModulus());
        nextEnrollment.setNric(enrollment.getNric());
        nextEnrollment.setSequenceNumber(0);
        nextEnrollment.setCompanyAddress(enrollment.getCompanyAddress());
        nextEnrollment.setCompanyName(enrollment.getCompanyName());
        nextEnrollment.setCompanyContact(enrollment.getCompanyContact());
        nextEnrollment.setStatus("new");
        nextEnrollment.setCountryBirth(enrollment.getCountryBirth());
            nextEnrollment.setDob(enrollment.getDob());
            nextEnrollment.setLate(false);
            nextEnrollment.setApproveReference(enrollment.getApproveReference());
        //nextEnrollment.setRegistrationFee(nextEnrollment.getBatch().getRegistrationFee());
        nextEnrollment.setApproveDate(enrollment.getApproveDate());
            nextEnrollment.setGender(enrollment.getGender());
             nextEnrollment.setPassportNo(enrollment.getPassportNo());
            nextEnrollment.setDesignation(enrollment.getDesignation());
            nextEnrollment.setAddressOffice(enrollment.getAddressOffice());
            nextEnrollment.setAddressHome(enrollment.getAddressHome());
            nextEnrollment.setPhoneOffice(enrollment.getPhoneOffice());
            nextEnrollment.setPhoneHome(enrollment.getPhoneHome());
            nextEnrollment.setEmail(enrollment.getEmail());
            nextEnrollment.setEmergencyName(enrollment.getEmergencyName());
            nextEnrollment.setEmergencyRelation(enrollment.getEmergencyRelation());
            nextEnrollment.setEmergencyPhone(enrollment.getEmergencyPhone());
            nextEnrollment.setKnowAboutProgram(enrollment.getKnowAboutProgram());
            nextEnrollment.setApplicationSignature(enrollment.getApplicationSignature());
            nextEnrollment.setPaidDate(enrollment.getPaidDate());
            nextEnrollment.setDate(enrollment.getDate());
            nextEnrollment.setCompany(enrollment.getCompany());
            nextEnrollment.setInstitutionDetails(enrollment.getInstitutionDetails());
            nextEnrollment.setApprove(enrollment.getApprove());
            nextEnrollment.setAmount(enrollment.getAmount());
            nextEnrollment.setPaymentStatus(enrollment.getPaymentStatus());
        nextEnrollment.setWorkPermitSpass(enrollment.getWorkPermitSpass());
        nextEnrollment.setRepass(enrollment.getRepass());
        nextEnrollment.setDependentPass(enrollment.getDependentPass());
        nextEnrollment.setPassport(enrollment.getPassport());
        nextEnrollment.setMarkSheet(enrollment.getMarkSheet());
        nextEnrollment.setCertificate(enrollment.getCertificate());
        nextEnrollment.setTranscript(enrollment.getTranscript());
        nextEnrollment.setProfilePicture(enrollment.getProfilePicture());
        nextEnrollment.setOtherDetails(enrollment.getOtherDetails());
        nextEnrollment.setReferalfriend(enrollment.getReferalfriend());


        List<ScheduleDate> objScheduler = calculateSubsequentModuliStartDatesStr(
                enrollment.getEnrolledDate(),
                startModulus,
                batchType
        );
            nextEnrollment.setScheduleDate(objScheduler);
            registrationAdvanceRepository.save(nextEnrollment);
       //     System.out.println("Entrolment Id :"+nextEnrollment.getEnrolledDate());
            return nextEnrollment;
    }


    public List<RegistrationAdvance> getRegistrationAdvancesByStudentId(Long studentId) {
        // Implement logic to fetch registration advances by student ID from your data source (e.g., database)
        return registrationAdvanceRepository.findByStudentId(studentId);
    }
    @Transactional
    public RegistrationAdvance updateSessionDetails(Long id, String sessionId, Long amount, String sessionIdDate, String paymentType) {
        Optional<RegistrationAdvance> optionalRegistrationAdvance = registrationAdvanceRepository.findById(id);
        if (optionalRegistrationAdvance.isPresent()) {
            RegistrationAdvance registrationAdvance = optionalRegistrationAdvance.get();
       //     registrationAdvance.setSessionId(sessionId);
            registrationAdvance.setAmount(amount);
       //     registrationAdvance.setSessionIdDate(sessionIdDate);
            registrationAdvance.setPaymentType(paymentType);
            return registrationAdvanceRepository.save(registrationAdvance);
        } else {
            return null;
        }
    }

//    public List<EnrollmentDTO> getEnrollmentsWithModuliByPaymentType(String paymentType) {
//        List<RegistrationAdvance> enrollments;
//        if(paymentType.equalsIgnoreCase("offline")) {
//            enrollments = registrationAdvanceRepository.findByPaymentType(paymentType);
//        } else {
//            enrollments = registrationAdvanceRepository.findAll();
//        }
//
//        Map<Long, EnrollmentDTO> enrollmentMap = new HashMap<>();
//
//        for (RegistrationAdvance enrollment : enrollments) {
//            Long studentId = enrollment.getStudent().getId();
//
//            if (!enrollmentMap.containsKey(studentId)) {
//                EnrollmentDTO enrollmentDTO = new EnrollmentDTO();
//                enrollmentDTO.setStudentName(enrollment.getStudent().getName());
//                enrollmentDTO.setStudentEmail(enrollment.getStudent().getEmail());
//                enrollmentDTO.setType(enrollment.getType());
//                enrollmentDTO.setTitle(enrollment.getTitle());
//                enrollmentDTO.setIntake(enrollment.getIntake());
//                enrollmentDTO.setName(enrollment.getName());
//                enrollmentDTO.setSureName(enrollment.getSureName());
//                enrollmentDTO.setPaymentType(enrollment.getPaymentType());
////                enrollmentDTO.setStartModulus(enrollment.getStartModulus());
//                enrollmentDTO.setNric(enrollment.getNric());
//
//                enrollmentDTO.setCountryBirth(enrollment.getCountryBirth());
//                enrollmentDTO.setDob(enrollment.getDob() != null ? LocalDate.parse(enrollment.getDob()) : null);
//                enrollmentDTO.setGender(enrollment.getGender());
//                enrollmentDTO.setNationality(enrollment.getNationality());
//        //        enrollmentDTO.setNRICno(enrollment.getNRICno());
//                enrollmentDTO.setPassportNo(enrollment.getPassportNo());
//                enrollmentDTO.setDesignation(enrollment.getDesignation());
//                enrollmentDTO.setAddressOffice(enrollment.getAddressOffice());
//                enrollmentDTO.setAddressHome(enrollment.getAddressHome());
//                enrollmentDTO.setPhoneOffice(enrollment.getPhoneOffice());
//                enrollmentDTO.setPhoneHome(enrollment.getPhoneHome());
//                enrollmentDTO.setEmail(enrollment.getEmail());
//                enrollmentDTO.setEmergencyName(enrollment.getEmergencyName());
//                enrollmentDTO.setEmergencyRelation(enrollment.getEmergencyRelation());
//                enrollmentDTO.setEmergencyPhone(enrollment.getEmergencyPhone());
//                enrollmentDTO.setKnowAboutProgram(enrollment.getKnowAboutProgram());
//                enrollmentDTO.setApplicationSignature(enrollment.getApplicationSignature());
//                enrollmentDTO.setDate(enrollment.getDate());
//                enrollmentDTO.setNonSingaporean(enrollment.getNonSingaporean());
//                enrollmentDTO.setCompany(enrollment.getCompany());
//        //        enrollmentDTO.setPhoto(enrollment.getPhoto());
//                enrollmentDTO.setApprove(enrollment.getApprove());
//          //      enrollmentDTO.setSessionId(enrollment.getSessionId());
//         //       enrollmentDTO.setSessionIdDate(enrollment.getSessionIdDate());
//                enrollmentDTO.setAmount(enrollment.getAmount());
//                enrollmentDTO.setPaymentStatus(enrollment.getPaymentStatus());
//                enrollmentDTO.setBatchId(enrollment.getBatch().getId());
//                enrollmentDTO.setStartModulus(enrollment.getStartModulus());
//                enrollmentDTO.setEnrollmentDate(enrollment.getEnrolledDate());
//                enrollmentDTO.setModuli(new ArrayList<>()); // Initialize the moduli list
//                enrollmentMap.put(studentId, enrollmentDTO);
//            }
//
//        }
//
//        return new ArrayList<>(enrollmentMap.values());
//    }

    @Transactional
    public RegistrationAdvance updateCourseFeeDetails(Long registrationId, List<CourseFeeDetailsDTO> feeDetailsDTOs) {
        Optional<RegistrationAdvance> optionalRegistrationAdvance = registrationAdvanceRepository.findById(registrationId);
        if (!optionalRegistrationAdvance.isPresent()) {
            throw new RuntimeException("RegistrationAdvance not found with id " + registrationId);
        }

        RegistrationAdvance registrationAdvance = optionalRegistrationAdvance.get();
        List<CourseFeeDetails> feeDetails = registrationAdvance.getCourseFeeDetails();
        feeDetails.clear();
        for (CourseFeeDetailsDTO dto : feeDetailsDTOs) {
            CourseFeeDetails feeDetail = new CourseFeeDetails();
            feeDetail.setSessionId(dto.getSessionId());
            feeDetail.setAmount(dto.getAmount());
            feeDetail.setPaymentMode(dto.getPaymentMode());
            feeDetail.setRemarks(dto.getRemarks());

            feeDetails.add(feeDetail);
        }

        registrationAdvance.setCourseFeeDetails(feeDetails);
        return registrationAdvanceRepository.save(registrationAdvance);
    }

    @Transactional
    public RegistrationAdvance addCourseFeeDetails(Long registrationId, CourseFeeDetailsDTO feeDetailsDTO) {
        Optional<RegistrationAdvance> optionalRegistrationAdvance = registrationAdvanceRepository.findById(registrationId);
        if (!optionalRegistrationAdvance.isPresent()) {
            throw new RuntimeException("RegistrationAdvance not found with id " + registrationId);
        }

        RegistrationAdvance registrationAdvance = optionalRegistrationAdvance.get();

        // Define the total fees
        double registrationFee = registrationAdvance.getRegistrationFee(); // Assuming you have a method to get registration fee
        double courseFee = registrationAdvance.getCourseFee(); // Assuming you have a method to get course fee

        // Define the total amount paid so far
        double totalPaidForRegistrationFee = registrationAdvance.getCourseFeeDetails().stream()
                .filter(detail -> "RegistrationFee".equals(detail.getType()))
                .mapToDouble(CourseFeeDetails::getAmount)
                .sum();

        double totalPaidForCourseFee = registrationAdvance.getCourseFeeDetails().stream()
                .filter(detail -> "CourseFee".equals(detail.getType()))
                .mapToDouble(CourseFeeDetails::getAmount)
                .sum();

        double remainingRegistrationFee = registrationFee - totalPaidForRegistrationFee;
        double remainingCourseFee = courseFee - totalPaidForCourseFee;

        // Get the payment amount from DTO
        double paymentAmount = feeDetailsDTO.getAmount();

        // First pay off the registration fee if any remaining
        if (remainingRegistrationFee > 0) {
            double paymentForRegistration = Math.min(paymentAmount, remainingRegistrationFee);
            CourseFeeDetails registrationFeeDetail = new CourseFeeDetails();
            registrationFeeDetail.setSessionId(feeDetailsDTO.getSessionId());
            registrationFeeDetail.setSessionDate(LocalDate.now());
            registrationFeeDetail.setAmount((long) paymentForRegistration);
            registrationFeeDetail.setPaymentMode(feeDetailsDTO.getPaymentMode());
            registrationFeeDetail.setRemarks(feeDetailsDTO.getRemarks());
            registrationFeeDetail.setStatus("Pending");
            registrationFeeDetail.setType("RegistrationFee");
            registrationAdvance.getCourseFeeDetails().add(registrationFeeDetail);

            paymentAmount -= paymentForRegistration;
        }

        // Apply remaining payment to course fee
        if (paymentAmount > 0 && remainingCourseFee > 0) {
            double paymentForCourse = Math.min(paymentAmount, remainingCourseFee);
            CourseFeeDetails courseFeeDetail = new CourseFeeDetails();
            courseFeeDetail.setSessionId(feeDetailsDTO.getSessionId());
            courseFeeDetail.setSessionDate(LocalDate.of(2024,11,01));
            courseFeeDetail.setAmount((long) paymentForCourse);
            courseFeeDetail.setPaymentMode(feeDetailsDTO.getPaymentMode());
            courseFeeDetail.setRemarks(feeDetailsDTO.getRemarks());
            courseFeeDetail.setStatus("Pending");
            courseFeeDetail.setType("CourseFee");
            registrationAdvance.getCourseFeeDetails().add(courseFeeDetail);
        }

        return registrationAdvanceRepository.save(registrationAdvance);
    }
    public RegistrationAdvance addCourseFeeDetailsStatus(Long registrationId) {
        // Retrieve the RegistrationAdvance entity
        Optional<RegistrationAdvance> optionalRegistrationAdvance = registrationAdvanceRepository.findById(registrationId);
        if (!optionalRegistrationAdvance.isPresent()) {
            throw new RuntimeException("RegistrationAdvance not found with id " + registrationId);
        }
        RegistrationAdvance registrationAdvance = optionalRegistrationAdvance.get();
        String name = registrationAdvance.getName();
        String email = registrationAdvance.getEmail();
        String phone = registrationAdvance.getPhoneHome();
        Long id = registrationAdvance.getId();
        String intakeId = registrationAdvance.getIntakeId();
        //Long amount= registrationAdvance.getCourseFeeDetails().getLast().getAmount();
        EnrollmentDTO enrollmentDTO = new EnrollmentDTO();
        enrollmentDTO.setBatchId(registrationAdvance.getBatch().getId());
//        String intakeIdWith = generateIntakeIdDetails(enrollmentDTO, LocalDate.now());
        String intakeIdWith = generateIntakeIdDetails(enrollmentDTO,  registrationAdvance.getScheduleDate().get(0).getStartDate());

//        System.out.println("intakeIdWith"+intakeIdWith);
//        System.out.println("intakeId"+intakeId);


        //Long amount= registrationAdvance.getCourseFeeDetails().getLast().getAmount();

        // If intakeId is null, use intakeIdWith as the fallback value
        String effectiveIntakeId = intakeId != null ? intakeId : intakeIdWith;

        Optional<IntakeDetails> existingIntakeDetails = intakeDetailsRepository.findByIntakeId(effectiveIntakeId);
        existingIntakeDetails.ifPresentOrElse(
                intakeDetails -> {
                    // IntakeDetails already exists, no action needed
                    System.out.println("IntakeDetails with intakeId " + effectiveIntakeId + " already exists.");
                },
                () -> {
                    // IntakeDetails does not exist, create and save a new entity
                    IntakeDetails newIntakeDetails = new IntakeDetails();
                    TimeTable newTimeTable = new TimeTable();
                    newIntakeDetails.setIntakeId(effectiveIntakeId);

                    // Set batchId and other details
                    newIntakeDetails.setBatchId(registrationAdvance.getBatch().getId());
                    newIntakeDetails.setCourse(registrationAdvance.getBatch().getCourse());
                    newIntakeDetails.setType(registrationAdvance.getBatch().getType());
                    newIntakeDetails.setLessonsPerWeek(registrationAdvance.getBatch().getLessonsPerWeek());
                    newIntakeDetails.setAssignment(registrationAdvance.getBatch().isAssignment());

                    newTimeTable.setCourse(registrationAdvance.getBatch().getCourse());
                    newTimeTable.setBatchId(registrationAdvance.getBatch().getId());
                    newTimeTable.setIntakeId(effectiveIntakeId);
                    newTimeTable.setType(registrationAdvance.getBatch().getType());



                    // Copy the moduleDetails list
                    List<ModuleDetail> copiedModuleDetail = registrationAdvance.getBatch().getModuleDetails().stream()
                            .map(md -> {
                                ModuleDetail moduleDetail = new ModuleDetail();
                                moduleDetail.setId(md.getId());
                                moduleDetail.setModuleName(md.getModuleName());
                                return moduleDetail;
                            })
                            .collect(Collectors.toList());

                    newTimeTable.setModuleDetails(copiedModuleDetail);



                    // Create deep copies of the assessors and trainers lists
                    List<String> copiedAssessors = new ArrayList<>(registrationAdvance.getBatch().getAssessors());
                    List<String> copiedTrainers = new ArrayList<>(registrationAdvance.getBatch().getTrainers());

                    newIntakeDetails.setAssessors(copiedAssessors);
                    newIntakeDetails.setTrainers(copiedTrainers);





                    List<ScheduleDateDetails> copiedScheduleDates = registrationAdvance.getScheduleDate().stream()
                            .map(sd -> {
                                ScheduleDateDetails scheduleDateDetails = new ScheduleDateDetails();
                                scheduleDateDetails.setStartModulus(sd.getStartModulus());
                                scheduleDateDetails.setStartDate(sd.getStartDate());
                                scheduleDateDetails.setEndDate(sd.getEndDate());
                                scheduleDateDetails.setVenue(registrationAdvance.getBatch().getVenue());


                                // Assign the first trainer from the trainers list
                                if (!copiedTrainers.isEmpty()) {
                                    scheduleDateDetails.setTrainer(copiedTrainers.get(0));
                                }

                                // Create a list of weeks
                                List<Week> weeks = new ArrayList<>();
                                int weekNumber = 1;

                                // Set the week start day (e.g., Monday)
                                DayOfWeek weekStartDay = DayOfWeek.MONDAY;

                                // Calculate the first week start date based on the start date
                                LocalDate currentDate = sd.getStartDate();
                                LocalDate endDate = sd.getEndDate();
                                // Adjust currentDate to the start of the first week (e.g., Monday)
                                currentDate = currentDate.with(TemporalAdjusters.previousOrSame(weekStartDay));

                                // Iterate through the entire date range week by week
                                while (!currentDate.isAfter(endDate)) {
                                    // Create a new week object
                                    Week week = new Week();
                                    week.setWeekNumber(weekNumber++);
                                    List<DateSession> dateSessions = new ArrayList<>();
                                    LocalDate startOfWeek = currentDate; // Start from the current week start day
                                    LocalDate endOfWeek = startOfWeek.plusDays(6); // Calculate the end of the week (7 days later)
                                    // Iterate over each day in the batch days list
                                    for (Day dayObj : registrationAdvance.getBatch().getDays()) {
                                        String specificDay = dayObj.getDay().toUpperCase(); // e.g., "FRIDAY"
                                        DayOfWeek targetDayOfWeek = DayOfWeek.valueOf(specificDay);

                                        // Find the first occurrence of the target day within the week
                                        LocalDate occurrenceInSameWeek = startOfWeek.with(TemporalAdjusters.nextOrSame(targetDayOfWeek));

                                        // Ensure the occurrence is within the module date range and the current week
                                        if (!occurrenceInSameWeek.isAfter(endDate) &&
                                                !occurrenceInSameWeek.isBefore(sd.getStartDate()) &&
                                                !occurrenceInSameWeek.isAfter(endOfWeek)) {
                                            DateSession dateSession = new DateSession();
                                            dateSession.setDate(occurrenceInSameWeek);
                                            dateSession.setDay(specificDay.toLowerCase());
                                            dateSession.setSessions(new HashMap<>(dayObj.getSessions())); // Use the sessions from the day object
                                            dateSessions.add(dateSession);
                                        }
                                    }
                                    // Handle the case where no valid date sessions are found within the current week
                                    if (dateSessions.isEmpty() && !startOfWeek.isAfter(endDate)) {
                                        DateSession dateSession = new DateSession();
                                        dateSession.setDate(startOfWeek); // Assign the week's start date
                                        dateSession.setDay(startOfWeek.getDayOfWeek().toString().toLowerCase());
                                        dateSession.setSessions(new HashMap<>()); // Empty sessions for a single date week
                                        dateSessions.add(dateSession);
                                    }
                                    // Assign the collected date sessions to the current week
                                    week.setDates(dateSessions);
                                    weeks.add(week);

                                    // Move to the next week
                                    currentDate = endOfWeek.plusDays(1); // Set current date to the day after the end of the current week
                                }
                                // Set the list of weeks to ScheduleDateDetails
                                scheduleDateDetails.setWeeks(weeks);

                                return scheduleDateDetails;
                            })
                            .collect(Collectors.toList());

                    newIntakeDetails.setScheduleDateDetails(copiedScheduleDates);
                    // Copy the scheduleDateDetails list with days and their sessions
                    List<ScheduleDateDetail> copiedScheduleDate = registrationAdvance.getScheduleDate().stream()
                            .map(sd -> {
                                ScheduleDateDetail scheduleDateDetail = new ScheduleDateDetail();
                                scheduleDateDetail.setStartModulus(sd.getStartModulus());
                                scheduleDateDetail.setStartDate(sd.getStartDate());
                                scheduleDateDetail.setEndDate(sd.getEndDate());
                                scheduleDateDetail.setVenue(registrationAdvance.getBatch().getVenue());

                                scheduleDateDetail.setStartTime(LocalTime.of(8, 0));
                                scheduleDateDetail.setEndTime(LocalTime.of(17, 30));
                                // Extract the day names from the days list in the batch
                                List<String> dayNames = registrationAdvance.getBatch().getDays().stream()
                                        .map(dayResponse -> dayResponse.getDay()) // Extract the day name
                                        .collect(Collectors.toList());

                                scheduleDateDetail.setDays(dayNames); // Set the list of day names

                                // Assign the first trainer from the trainers list
                                if (!copiedTrainers.isEmpty()) {
                                    scheduleDateDetail.setTrainer(copiedTrainers.get(0));
                                }

                                return scheduleDateDetail;
                            })
                            .collect(Collectors.toList());

                    newTimeTable.setScheduleDateDetails(copiedScheduleDate);
                    // Copy the moduleDetails list
                    List<ModuleDetails> copiedModuleDetails = registrationAdvance.getBatch().getModuleDetails().stream()
                            .map(md -> {
                                ModuleDetails moduleDetails = new ModuleDetails();
                                moduleDetails.setId(md.getId());
                                moduleDetails.setModuleName(md.getModuleName());
                                return moduleDetails;
                            })
                            .collect(Collectors.toList());

                    newIntakeDetails.setModuleDetails(copiedModuleDetails);

                    // Set the days list
//                    newIntakeDetails.setDays(new ArrayList<>(registrationAdvance.getBatch().getDays()));
//
//                    // Set the sessions map
//                    newIntakeDetails.setSessions(new HashMap<>(registrationAdvance.getBatch().getSessions()));

                    intakeDetailsRepository.save(newIntakeDetails);
                    timeTableRepository.save(newTimeTable);
                }
        );

        // Find existing CourseFeeDetails with status "pending"
        List<CourseFeeDetails> courseFeeDetailsList = registrationAdvance.getCourseFeeDetails();
        boolean updated = false;
        String previousIntakeId = registrationAdvance.getBatch().getIntakeId();

// Variables to track the last "Pending" items for each type
        int lastRegistrationFeeIndex = -1;
        int lastCourseFeeIndex = -1;

// Variables to track installment counts

        int courseFeeInstallmentCount = 0;

// First Loop: Find the last "Pending" items and update their status
        for (int i = courseFeeDetailsList.size() - 1; i >= 0; i--) {
            CourseFeeDetails feeDetail = courseFeeDetailsList.get(i);

            if ("Pending".equals(feeDetail.getStatus())) {
                if ("RegistrationFee".equals(feeDetail.getType())) {
                    lastRegistrationFeeIndex = i;
                } else if ("CourseFee".equals(feeDetail.getType())) {
                    lastCourseFeeIndex = i;
                }
            }
        }

// Update the status of the last "Pending" item for each type
        if (lastRegistrationFeeIndex != -1) {
            CourseFeeDetails regFeeDetail = courseFeeDetailsList.get(lastRegistrationFeeIndex);
            regFeeDetail.setStatus("Paid");
            updated = true;
        }

        if (lastCourseFeeIndex != -1) {
            CourseFeeDetails courseFeeDetail = courseFeeDetailsList.get(lastCourseFeeIndex);
            courseFeeDetail.setStatus("Paid");
            updated = true;
        }

// If updated, set additional fields like intakeId, studentIntakeId, etc.
        if (updated) {
            registrationAdvance.setPaidDate(LocalDateTime.now());

            // Generate intakeId and other relevant fields if necessary
            registrationAdvance.setIntakeId(enrollmentDTO.getIntakeId());

            String currentIntakeId = batchRepository.findById(enrollmentDTO.getBatchId())
                    .map(Batch::getIntakeId)
                    .orElse(previousIntakeId);

            Long studentIntakeId;
            if (previousIntakeId == null || previousIntakeId.equals(currentIntakeId)) {
                studentIntakeId = getNextCustomId(registrationAdvance.getBatch().getId(), enrollmentDTO.getIntakeId());
            } else {
                studentIntakeId = 1L;
            }

            // Create or update the StudentId entity
            StudentId studentId = studentIdRepository.findByBatchIdAndBatchIntakeId(
                            registrationAdvance.getBatch().getId(), registrationAdvance.getBatch().getIntakeId())
                    .orElseGet(() -> {
                        StudentId newStudentId = new StudentId();
                        newStudentId.setBatchId(registrationAdvance.getBatch().getId());
                        newStudentId.setBatchIntakeId(registrationAdvance.getBatch().getIntakeId());
                        newStudentId.setLastId(studentIntakeId);
                        return newStudentId;
                    });

            // Save or update the StudentId entity
            studentId.setLastId(studentIntakeId);
            studentIdRepository.save(studentId);

            String studentIntakeIdString = String.valueOf(studentIntakeId);
            String courseName = registrationAdvance.getBatch().getCourse();

            // List of words to omit when generating the course short form
            List<String> omitWords = Arrays.asList("in", "to", "for", "as", "and", "of", "from",
                    "part-time", "full-time", "PartTime", "FullTime");

            // Generate course name abbreviation
            String[] words = courseName.split("\\s+");
            String firstLetters = "";
            for (String word : words) {
                if (!omitWords.contains(word)) {
                    firstLetters += word.charAt(0); // Append the first character
                }
            }
            String result = firstLetters.toUpperCase();

            LocalDate currentDate1 = registrationAdvance.getScheduleDate().get(0).getStartDate();
            String monthInWords = currentDate1.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase();
            int year = currentDate1.getYear();
            String lastTwoDigits = String.format("%02d", year % 100);
            String monthYear = monthInWords + "-" + lastTwoDigits;
            String fullIntakeId = result + "-" + monthYear + "-" + studentIntakeIdString;

            registrationAdvance.setStudentIntakeId(fullIntakeId);

            updated = true;
        }

// Second Loop: Calculate remaining balances and set sequence number if needed
        // Assuming these variables are initialized

// Second Loop: Calculate remaining balances and set sequence number if needed

// Second Loop: Calculate remaining balances and set sequence number if needed
        // Assuming these variables are initialized

// Second Loop: Calculate remaining balances and set sequence number if needed
        // Assuming these variables are initialized
        // Initialize variables

        CourseFeeDetails lastPaidDetail = null;
        boolean sequenceGenerated = false;
        long registrationFeeRemaining = registrationAdvance.getRegistrationFee();
        long courseFeeRemaining = registrationAdvance.getBatch().getCourseFee();
        int totalInstallments;

        if (registrationAdvance.getType().equalsIgnoreCase("diploma")) {
            totalInstallments = 12;
        } else if (registrationAdvance.getType().equalsIgnoreCase("advance_diploma")) {
            totalInstallments = 6;
        } else {
            // Default value or handle other course types
            totalInstallments = 0; // or any other default value
        }
        long installmentAmount = courseFeeRemaining / totalInstallments; // Amount per installment
        int currentInstallment = 1; // Track the current installment
        long amountAppliedToCurrentInstallment = 0;
        long discountAmount =0;
// List to track completed and partially paid installments
        List<Integer> completedInstallments = new ArrayList<>();
        List<Integer> partiallyPaidInstallments = new ArrayList<>();
        boolean discountApplied = false;

// Iterate through each payment (feeDetails)
        for (CourseFeeDetails feeDetails : courseFeeDetailsList) {
            if ("Paid".equalsIgnoreCase(feeDetails.getStatus())) {
                Long amountPaid = feeDetails.getAmount();

                if (amountPaid != null) {
                    // Handle registration fee payments
                    if (registrationFeeRemaining > 0 && "RegistrationFee".equals(feeDetails.getType())) {
                        // Registration fee handling logic (same as before)
                        if (amountPaid <= registrationFeeRemaining) {
                            registrationFeeRemaining -= amountPaid;
                            feeDetails.setRegistrationFeeRemaining(registrationFeeRemaining);
                        } else {
                            long remainingAmount = amountPaid - registrationFeeRemaining;
                            registrationFeeRemaining = 0;
                            feeDetails.setRegistrationFeeRemaining(0L);
                            if (courseFeeRemaining > 0) {
                                courseFeeRemaining -= remainingAmount;
                            }
                        }
                        feeDetails.setInstallmentCount(null);
                        feeDetails.setCourseFeeRemaining(courseFeeRemaining);
                    }

                    // Handle course fee payments
                    else if (courseFeeRemaining > 0 && "CourseFee".equals(feeDetails.getType())) {
                        // Apply discount to the current installment
                        if (!discountApplied && discountAmount > 0) {
                            long discountedInstallmentAmount = installmentAmount;

                            // Apply discount only to the current installment
                            if (discountAmount >= installmentAmount - amountAppliedToCurrentInstallment) {
                                discountAmount -= (installmentAmount - amountAppliedToCurrentInstallment);
                                amountAppliedToCurrentInstallment = installmentAmount;
                            } else {
                                amountAppliedToCurrentInstallment += discountAmount;
                                discountAmount = 0;
                            }

                            discountApplied = true; // Ensure discount is applied once
                            courseFeeRemaining -= discountAmount; // Deduct discount from course fee remaining
                        }

                        // Add the paid amount after discount is applied
                        amountAppliedToCurrentInstallment += amountPaid;
                        courseFeeRemaining -= amountPaid;

                        partiallyPaidInstallments.clear();
                        completedInstallments.clear();

                        // Process payments for multiple installments
                        while (amountAppliedToCurrentInstallment >= installmentAmount) {
                            completedInstallments.add(currentInstallment);
                            amountAppliedToCurrentInstallment -= installmentAmount;
                            currentInstallment++;
                        }

                        // Track partially paid installments
                        if (amountAppliedToCurrentInstallment > 0) {
                            partiallyPaidInstallments.add(currentInstallment);
                        }

                        // Update remaining balance for the current installment
                        if (amountAppliedToCurrentInstallment == 0) {
                            feeDetails.setRemainingBalance(0L);
                        } else {
                            feeDetails.setRemainingBalance(installmentAmount - amountAppliedToCurrentInstallment);
                        }

                        // Update installment details
                        feeDetails.setInstallmentCount(currentInstallment);
                        feeDetails.setCourseFeeRemaining(courseFeeRemaining);
                        feeDetails.setRegistrationFeeRemaining(registrationFeeRemaining);

                        // Set installmentCounts for the current feeDetails
                        List<Integer> currentInstallments = new ArrayList<>(completedInstallments);
                        currentInstallments.addAll(partiallyPaidInstallments);
                        feeDetails.setInstallmentCounts((ArrayList<Integer>) currentInstallments);
                    }
                }
            }
        }
//
//
// Sequence number generation logic can be added here if necessary
            if (lastPaidDetail != null && lastPaidDetail.getSequenceNumber() == 0 && courseFeeRemaining > 0 && courseFeeRemaining != registrationAdvance.getCourseFee() && !sequenceGenerated) {
                int sequenceNumber = SequenceGenerator.getNextSequenceNumber();
                lastPaidDetail.setSequenceNumber(sequenceNumber);
                registrationAdvance.setSequenceNumber(sequenceNumber);
                sequenceGenerated = true;
            }

        // Set the sequence number to 0 if no sequence was generated and fees are paid off
        if (!sequenceGenerated && registrationFeeRemaining <= 0 && courseFeeRemaining <= 0) {
            registrationAdvance.setSequenceNumber(0);
        }

        // Save only if an update was made
        if (updated) {
            // Save the updated RegistrationAdvance entity
            registrationAdvanceRepository.save(registrationAdvance);

            Long amount = lastPaidDetail != null ? lastPaidDetail.getAmount() : null;
            //  System.out.println("ammount"+ amount);
            String intakeId1 = registrationAdvance.getIntakeId();
            // Create and save an AdminNotification entity
            AdminNotification adminNotification = new AdminNotification(
                    name,
                    email,
                    phone,
                    id.toString(),
                    // Assuming studentId is the same as the RegistrationAdvance id
                    "Course fee status updated to 'paid'.",
                    amount,
                    intakeId1
            );

            adminNotificationRepository.save(adminNotification);
        }



        return registrationAdvance;
    }
    public Long getNextCustomId(Long batchId, String batchIntakeId) {
        // Find the existing StudentId entity by batchId and batchIntakeId
        StudentId studentId = studentIdRepository.findByBatchIdAndBatchIntakeId(batchId, batchIntakeId)
                .orElseGet(() -> {
                    // If no existing StudentId, create a new one
                    StudentId newTracker = new StudentId();
                    newTracker.setBatchId(batchId);
                    newTracker.setBatchIntakeId(batchIntakeId);
                    newTracker.setLastId(0L); // Start with 0; will be incremented to 1
                    return studentIdRepository.save(newTracker);
                });

        // Generate new custom ID by incrementing the last ID
        Long newCustomId = studentId.getLastId() + 1;

        // Update the StudentId entity with the new last ID
        studentId.setLastId(newCustomId);
        studentIdRepository.save(studentId);

        // Return the new custom ID
        return newCustomId;
    }

    public List<ScheduleDateWithStudentInfoDTO> getScheduleDatesWithStudentInfoByBatchId(Long batchId) {
        return registrationAdvanceRepository.findScheduleDatesWithStudentInfoByBatchId(batchId);
    }
//    public List<StudentScheduleDTO> getApprovedScheduleDatesWithStudentInfoByBatchId(Long batchId) {
//        List<ScheduleDateWithStudentInfoDTO> scheduleDatesWithStudentInfoList =
//                registrationAdvanceRepository.findScheduleDatesWithStudentInfoByBatchId(batchId);
//
//        Map<String, StudentScheduleDTO> studentScheduleMap = new HashMap<>();
//
//        for (ScheduleDateWithStudentInfoDTO scheduleDateWithStudentInfo : scheduleDatesWithStudentInfoList) {
//            if (isCurrentMonth(scheduleDateWithStudentInfo.getStartDate())) {
//                String studentKey = generateStudentKey(scheduleDateWithStudentInfo);
//                StudentScheduleDTO studentSchedule = studentScheduleMap.get(studentKey);
//
//                if (studentSchedule == null) {
//                    StudentInfoDTO studentInfo = new StudentInfoDTO(
//                            scheduleDateWithStudentInfo.getStudentName(),
//                            scheduleDateWithStudentInfo.getEmail(),
//                            scheduleDateWithStudentInfo.getStudentPhoneNumber()
//                    );
//
//                    Set<ScheduleDateDTO> scheduleDates = new HashSet<>();
//                    scheduleDates.add(new ScheduleDateDTO(
//                            scheduleDateWithStudentInfo.getStartModulus(),
//                            scheduleDateWithStudentInfo.getStartDate(),
//                            scheduleDateWithStudentInfo.getEndDate()
//                    ));
//
//                    studentScheduleMap.put(studentKey, new StudentScheduleDTO(
//                            scheduleDateWithStudentInfo.getId(), // Set the id here
//                            studentInfo,
//                            new ArrayList<>(scheduleDates)
//                    ));
//                } else {
//                    studentSchedule.getScheduleDates().add(new ScheduleDateDTO(
//                            scheduleDateWithStudentInfo.getStartModulus(),
//                            scheduleDateWithStudentInfo.getStartDate(),
//                            scheduleDateWithStudentInfo.getEndDate()
//                    ));
//                }
//            }
//        }
//
//        return new ArrayList<>(studentScheduleMap.values());
//    }

    private boolean isCurrentMonth(LocalDate date) {
        YearMonth currentMonth = YearMonth.now();
        YearMonth dateMonth = YearMonth.from(date);
        return dateMonth.equals(currentMonth);
    }

    private String generateStudentKey(ScheduleDateWithStudentInfoDTO scheduleDateWithStudentInfo) {
        return scheduleDateWithStudentInfo.getStudentName() + "-" +
                scheduleDateWithStudentInfo.getEmail() + "-" +
                scheduleDateWithStudentInfo.getStudentPhoneNumber() + "-" +
                String.valueOf(scheduleDateWithStudentInfo.getId());
    }
    public List<RegistrationAdvancesDetailsDTO> getAllRegistrationsAndAlumni() {
        List<Student> students = studentRepository.findAll();
        List<RegistrationAdvancesDetailsDTO> dtos = new ArrayList<>();
        LocalDate currentDate = LocalDate.now(); // Manually set date

        for (Student student : students) {
            List<RegistrationAdvance> registrations = registrationAdvanceRepository.findByStudentId(student.getId());
            for (RegistrationAdvance registration : registrations) {
                List<ScheduleDate> scheduleDates = registration.getScheduleDate();
                updateStatusIfNecessary(student, scheduleDates, currentDate);

                RegistrationAdvanceInfo info = populateRegistrationAdvanceInfo(registration);

                if ("Alumni".equalsIgnoreCase(student.getStatus())
                        && info.getRegistrationFeeRemaining() == 0
                        && info.getCourseFeeRemaining() == 0) {
                    registration.setStatus("Alumni");
                    registrationAdvanceRepository.save(registration);
                    dtos.add(convertToDTO(registration, scheduleDates));
                }
            }
        }

        return dtos;
    }

    private void updateStatusIfNecessary(Student student, List<ScheduleDate> scheduleDates, LocalDate currentDate) {
        if (scheduleDates.isEmpty()) {
            return;
        }
//        currentDate=LocalDate.of(2025,11,10);
        LocalDate overallEndDate = scheduleDates.get(scheduleDates.size() - 1).getEndDate();
        if (currentDate.isAfter(overallEndDate)) {
            student.setStatus("Alumni");
            studentRepository.save(student);
        }
    }

    public RegistrationAdvancesDetailsDTO convertToDTO(RegistrationAdvance registration, List<ScheduleDate> scheduleDates) {
        RegistrationAdvancesDetailsDTO dto = new RegistrationAdvancesDetailsDTO();
        Student student = registration.getStudent();
        dto.setRegistrationId(registration.getId());
        RegistrationAdvanceInfo info = populateRegistrationAdvanceInfo(registration);
        dto.setStudentName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setPhone(student.getPhone());
        dto.setGender(student.getGender());
        dto.setDob(student.getDob().toString());
        dto.setRegistrationFeeRemaining(info.getRegistrationFeeRemaining());
        dto.setCourseFeeRemaining(info.getCourseFeeRemaining());
        dto.setStatus(student.getStatus());
        dto.setNric(student.getNric());
//       dto.setBlanceAmount(info.getTotalPaidAmount());

        // Convert schedule dates
        List<String> scheduleDatesStr = new ArrayList<>();
        for (int i = 0; i < scheduleDates.size(); i++) {
            ScheduleDate scheduleDate = scheduleDates.get(i);
            String dateRange = String.format("%d\t%s\t%s\t%d",
                    (i + 1),
                    scheduleDate.getStartDate(),
                    scheduleDate.getEndDate(),
                    (i + registration.getStartModulus()) % (registration.getEndModulus() == 0 ? 1 : registration.getEndModulus()));
            scheduleDatesStr.add(dateRange);
        }

        // Set overall start and end dates
        if (!scheduleDates.isEmpty()) {
            LocalDate overallStartDate = scheduleDates.get(0).getStartDate();
            dto.setOverallStartDate(overallStartDate.toString());
            dto.setOverallEndDate(scheduleDates.get(scheduleDates.size() - 1).getEndDate().toString());

            // Format the intake ID as MONTH-YEAR
            String intakeId = overallStartDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase() + "-" + overallStartDate.getYear();
            dto.setIntakeId(intakeId);
            info.setIntakeId(intakeId); // Ensure intakeId is set in info
        }

        return dto;
    }

    public List<RegistrationAdvancesDetailsDTO> getsAllRegistrationsAndAlumni() {
        List<Student> students = studentRepository.findAll();
        List<RegistrationAdvancesDetailsDTO> dtos = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        for (Student student : students) {
            List<RegistrationAdvance> registrations = registrationAdvanceRepository.findByStudentId(student.getId());
            for (RegistrationAdvance registration : registrations) {
                List<ScheduleDate> scheduleDates = registration.getScheduleDate();
                updateStatusIfNecessary(student, scheduleDates, currentDate);

                RegistrationAdvanceInfo info = populateRegistrationAdvanceInfo(registration);

                if ("Alumni".equalsIgnoreCase(student.getStatus())
                        && info.getRegistrationFeeRemaining() > 0
                        && info.getCourseFeeRemaining() > 0) {
                    registration.setStatus("Alumni");
                    registrationAdvanceRepository.save(registration);
                    dtos.add(convertToDTO(registration, scheduleDates));
                }
            }
        }

        return dtos;
    }
    private void updateStatusIfNecessarys(Student student, List<ScheduleDate> scheduleDates, LocalDate currentDate) {
        if (scheduleDates.isEmpty()) {
            return;
        }
        LocalDate overallEndDate = scheduleDates.get(scheduleDates.size() - 1).getEndDate();
        if (currentDate.isAfter(overallEndDate)) {
            student.setStatus("Alumni");
            studentRepository.save(student);
        }
    }

    public RegistrationAdvancesDetailsDTO convertToDTOs(RegistrationAdvance registration, List<ScheduleDate> scheduleDates) {
        RegistrationAdvancesDetailsDTO dto = new RegistrationAdvancesDetailsDTO();
        Student student = registration.getStudent();

        dto.setStudentName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setPhone(student.getPhone());
        dto.setGender(student.getGender());
        //      dto.setAddress(student.getAddress());
        dto.setDob(student.getDob().toString());
        dto.setStatus(student.getStatus());
        dto.setNric(student.getNric());

        // Convert schedule dates
        List<String> scheduleDatesStr = new ArrayList<>();
        for (int i = 0; i < scheduleDates.size(); i++) {
            ScheduleDate scheduleDate = scheduleDates.get(i);
            String dateRange = String.format("%d\t%s\t%s\t%d",
                    (i + 1),
                    scheduleDate.getStartDate(),
                    scheduleDate.getEndDate(),
                    (i + registration.getStartModulus()) % (registration.getEndModulus() == 0 ? 1 : registration.getEndModulus()));
            scheduleDatesStr.add(dateRange);
        }


        // Set overall start and end dates
        if (!scheduleDates.isEmpty()) {
            dto.setOverallStartDate(scheduleDates.get(0).getStartDate().toString());
            dto.setOverallEndDate(scheduleDates.get(scheduleDates.size() - 1).getEndDate().toString());
        }

        return dto;
    }



    public RegistrationAdvanceInfo getRegistrationAdvanceInfoAndSendEmail(Long id) throws MessagingException, IOException {
        RegistrationAdvance registrationAdvance = getRegistrationAdvanceById(id);
        if (registrationAdvance != null) {
            // Check if the email has already been sent
            RegistrationAdvanceInfo info = populateRegistrationAdvanceInfo(registrationAdvance);


            // If email has not been sent yet, proceed to send it
            if (!info.isEmailSent()) {
                // Fetch email from registrationAdvance
                String email = registrationAdvance.getEmail();

                String courseName = info.getCourse().replaceAll("\\(.*?\\)", "").trim();


                // Default subject and text
                String subject = "Invoice for " + courseName  + " Registration";
                String text = "<html><body style='font-family: Arial, sans-serif;'>" +
                        "<div style='max-width: 600px; margin: 0 auto;'>" +
                        "<h2 style='color: #004c8c;'>Invoice for " + courseName  + " Registration</h2>" +
                        "<p style='font-size: 14px;'>Dear " + info.getName() + ",</p>" +
                        "<p style='font-size: 14px;'>Thank you for registering for the <strong>" + courseName + "</strong> course.</p>" +
                        "<p style='font-size: 14px;'>Please find attached your invoice details.</p>" +
                        "<table style='width: 100%; border-collapse: collapse; margin-top: 20px;'>" +
                        "<tr><td style='padding: 8px; border: 1px solid #ddd;'><b>Registration Fee:</b></td><td style='padding: 8px; border: 1px solid #ddd;'>" + String.format("%,d", registrationAdvance.getRegistrationFee().longValue()) + "</td></tr>" +
                        "<tr><td style='padding: 8px; border: 1px solid #ddd;'><b>Course Fee:</b></td><td style='padding: 8px; border: 1px solid #ddd;'>" + String.format("%,d", registrationAdvance.getCourseFee().longValue()) + "</td></tr>" +
                        "<tr><td style='padding: 8px; border: 1px solid #ddd;'><b>Total Fee:</b></td><td style='padding: 8px; border: 1px solid #ddd;'>" + String.format("%,d", (registrationAdvance.getCourseFee() + registrationAdvance.getRegistrationFee().longValue())) + "</td></tr>" +
                        "</table>" +
                        "<p style='font-size: 14px; margin-top: 20px;'>Best regards,</p>" +
                        "<p style='font-size: 14px;'>Jurong Academy</p>" +
                        "<div style='margin-top: 20px; border-top: 1px solid #ddd; padding-top: 10px;'>" +
                        "<p style='font-size: 14px;'><b>Address:</b><br>" +
                        "Jurong Academy<br>" +
                        "Reg. No: 201322342Z<br>" +
                        "134 Jurong Gateway Road, #03-307L (Head Office),<br>" +
                        "#03-309P (Additional Premises), Singapore 600134.</p>" +
                        "<p style='font-size: 14px;'><b>Contact Information:</b><br>" +
                        "Admission Enquiry: (65) 9180 6070<br>" +
                        "Tel: (65) 6635 5724<br>" +
                        "Email: <a href='mailto:ask@ja.edu.sg' style='color: #004c8c;'>ask@ja.edu.sg</a>, <a href='mailto:jurongacademy@gmail.com' style='color: #004c8c;'>jurongacademy@gmail.com</a></p>" +
                        "</div>" +
                        "</div>" +
                        "</body></html>";


                CourseFeeDetails installmentDetails=new CourseFeeDetails();
                // Send email with fetched email, default subject, and text

                sendInvoiceEmail(email, subject, text, info,registrationAdvance,installmentDetails);

                // Mark email as sent in the RegistrationAdvance entity
                registrationAdvance.setEmailSent(true);
               //  info.setMessage("Email sent successfully to " + email + ".");
                // Update registrationAdvance in the database if necessary
                registrationAdvanceRepository.save(registrationAdvance);
            }
            return info;

        } else {
            return null;
        }
    }

    public RegistrationAdvanceInfo populateRegistrationAdvanceInfo(RegistrationAdvance registrationAdvance) {
        RegistrationAdvanceInfo info = new RegistrationAdvanceInfo();

        // Set basic info
        info.setId(registrationAdvance.getId());
        info.setName(registrationAdvance.getName());
        info.setPaymentType(registrationAdvance.getPaymentType());
        info.setStartModulus(registrationAdvance.getStartModulus());
        info.setNric(registrationAdvance.getNric());
        info.setAddressHome(registrationAdvance.getAddressHome());
        info.setStudentId(registrationAdvance.getStudent().getId());
        info.setDate(registrationAdvance.getDate());
        info.setEmail(registrationAdvance.getEmail());
        info.setRegistrationFee(registrationAdvance.getBatch().getRegistrationFee());
        info.setCourseFee(registrationAdvance.getBatch().getCourseFee());
        info.setPassportNo(registrationAdvance.getPassportNo());
        info.setCourse(registrationAdvance.getBatch().getCourse());
        info.setIntakeId(registrationAdvance.getIntakeId());
        info.setNric(registrationAdvance.getStudent().getNric());
        info.setApprover(registrationAdvance.getApprover());

        // Separate lists for RegistrationFee and CourseFee
        List<CourseFeeDetails> registrationFeeDetailsList = new ArrayList<>();
        List<CourseFeeDetails> courseFeeDetailsList = new ArrayList<>();

        // Use a forEach loop to iterate over courseFeeDetails
        registrationAdvance.getCourseFeeDetails().forEach(courseFeeDetail -> {
            CourseFeeDetails details = new CourseFeeDetails();

            // Copy properties
            details.setSessionId(courseFeeDetail.getSessionId());
            details.setSessionDate(courseFeeDetail.getSessionDate());
            details.setAmount(courseFeeDetail.getAmount());
            details.setPaymentMode(courseFeeDetail.getPaymentMode());
            details.setRemarks(courseFeeDetail.getRemarks());
            details.setStatus(courseFeeDetail.getStatus());
            details.setInstallmentCounts(courseFeeDetail.getInstallmentCounts());
            details.setCourseFeeRemaining(courseFeeDetail.getCourseFeeRemaining());
            details.setRegistrationFeeRemaining(courseFeeDetail.getRegistrationFeeRemaining());
            details.setEmailSent(courseFeeDetail.isEmailSent());
            details.setSequenceNumber(courseFeeDetail.getSequenceNumber());
            details.setEmailCourseSent(courseFeeDetail.isEmailCourseSent());
            details.setType(courseFeeDetail.getType());
            details.setRemainingBalance(courseFeeDetail.getRemainingBalance());

            // Separate based on fee type
            if ("RegistrationFee".equalsIgnoreCase(courseFeeDetail.getType())) {
                registrationFeeDetailsList.add(details);
            } else if ("CourseFee".equalsIgnoreCase(courseFeeDetail.getType())) {
                courseFeeDetailsList.add(details);
            }
        });



        // Set the lists in the RegistrationAdvanceInfo object
        info.setInstallmentsPaid(registrationFeeDetailsList);
        info.setCourseFeeDetails(courseFeeDetailsList);

        // Additional processing for the last elements
        if (!courseFeeDetailsList.isEmpty()) {
            CourseFeeDetails lastCourseFeeDetail = courseFeeDetailsList.get(courseFeeDetailsList.size() - 1);
            info.setCourseFeeRemaining(lastCourseFeeDetail.getCourseFeeRemaining());
            info.setRegistrationFeeRemaining(lastCourseFeeDetail.getRegistrationFeeRemaining());
            info.setSequenceNumber(lastCourseFeeDetail.getSequenceNumber());
        }

        if (!registrationFeeDetailsList.isEmpty()) {
            CourseFeeDetails lastRegistrationFeeDetail = registrationFeeDetailsList.get(registrationFeeDetailsList.size() - 1);
            info.setRegistrationFeeRemaining(lastRegistrationFeeDetail.getRegistrationFeeRemaining());
            info.setCourseFeeRemaining(lastRegistrationFeeDetail.getCourseFeeRemaining());
        }
        if (registrationAdvance.getScheduleDate() != null && !registrationAdvance.getScheduleDate().isEmpty()) {
            // Assuming scheduleDate is sorted or you want the first entry
            ScheduleDate startModuleSchedule = registrationAdvance.getScheduleDate().get(0); // get the first schedule date
            info.setStartModuleDate(startModuleSchedule.getStartDate()); // Assuming ScheduleDate has a getDate method
        }
        return info;
    }

    private void sendInvoiceEmail(String to, String subject, String text, RegistrationAdvanceInfo info,RegistrationAdvance registrationAdvance,CourseFeeDetails installmentDetails) throws MessagingException, IOException {
        if (info.isEmailSent()) {
            return; // Skip sending email if already sent
        }
        List<ScheduleDate> scheduleDates = registrationAdvance.getScheduleDate();
        ClassPathResource pdfResource = new ClassPathResource("invoice.pdf");
        ByteArrayOutputStream outputStream = modifyPdf(pdfResource.getInputStream(), info,registrationAdvance,scheduleDates,installmentDetails);

        // Create file name based on info.getName()
        String fileName = info.getName().replaceAll("\\s+", "_") + "_invoice.pdf";
        File tempFile = new File(System.getProperty("java.io.tmpdir"), fileName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            outputStream.writeTo(fos);
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // Ensure UTF-8 encoding
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true); // Set the email body as HTML

        // Attach the file with the name based on info.getName()
        helper.addAttachment(fileName, new ByteArrayResource(Files.readAllBytes(tempFile.toPath())));
        mailSender.send(message);

        // Clean up temporary file
        tempFile.delete();
    }






    private ByteArrayOutputStream modifyPdf(
            InputStream pdfInvoice,
            RegistrationAdvanceInfo info,
            RegistrationAdvance registrationAdvance,
            List<ScheduleDate> scheduleDates,
            CourseFeeDetails installmentDetails) throws IOException {

        PDDocument document = PDDocument.load(pdfInvoice);
        PDPage page = document.getPage(0);
        LocalDate overallStartDate = null;

        if (scheduleDates != null && !scheduleDates.isEmpty()) {
            overallStartDate = scheduleDates.get(0).getStartDate();
        }

        // Load the Calibri font from resources
        InputStream fontStream = getClass().getResourceAsStream("/calibri.ttf");
        PDType0Font calibri = PDType0Font.load(document, fontStream);

        PDPageContentStream contentStream = new PDPageContentStream(
                document,
                page,
                PDPageContentStream.AppendMode.APPEND,
                true
        );

        // Remove brackets and content inside them from the course name
        String courseName = info.getCourse().replaceAll("\\(.*?\\)", "").trim();

        // Convert the date format
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(info.getDate(), inputFormatter);
        String formattedDate;

        if (info.isLate() && overallStartDate != null) {
            formattedDate = overallStartDate.format(outputFormatter);
        } else {
            formattedDate = date.format(outputFormatter);
        }

        contentStream.beginText();
        contentStream.setFont(calibri, 12);
        contentStream.setNonStrokingColor(0, 0, 0);
        contentStream.newLineAtOffset(118, 608);
        contentStream.showText(info.getName().toUpperCase());
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText(info.getNric().toUpperCase());

        // Set font for the course name and handle text wrapping
        contentStream.setFont(calibri, 12);
        contentStream.newLineAtOffset(0, -15);

        // Define the maximum width for a line
        float maxWidth = 200; // adjust this value based on your page width
        float textWidth = calibri.getStringWidth(courseName.toUpperCase()) / 800 * 10; // calculate the text width

        if (textWidth > maxWidth) {
            // The course name is too long, so split it into multiple lines
            String[] words = courseName.split(" ");
            StringBuilder currentLine = new StringBuilder();
            List<String> lines = new ArrayList<>();

            for (String word : words) {
                // Check if adding the next word would exceed the maxWidth
                String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
                float testLineWidth = calibri.getStringWidth(testLine.toUpperCase()) / 1000 * 10;
                if (testLineWidth > maxWidth) {
                    // Current line is full, save it and start a new line
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    // Add the word to the current line
                    if (currentLine.length() > 0) {
                        currentLine.append(" ");
                    }
                    currentLine.append(word);
                }
            }
            // Add the last line
            lines.add(currentLine.toString());

            // Write the lines to the PDF
            for (String line : lines) {
                contentStream.showText(line.toUpperCase());
                contentStream.newLineAtOffset(0, -15); // Move to the next line
            }
        } else {
            // The course name fits in one line
            contentStream.showText(courseName.toUpperCase());
            contentStream.newLineAtOffset(0, -29);
        }

        contentStream.setFont(calibri, 12); // reset the font size for the next text

//
//        // Check if there are at least two parts
//        if (parts.length == 2) {
//            String firstPart = parts[0]; // First part before the hyphen
//            String secondPart = parts[1]; // Second part after the hyphen
//
//            // Keep only the first two characters of the second part
//            if (secondPart.length() > 2) {
//                secondPart = secondPart.substring(0, 2);
//            }
//
//            // Concatenate the first part with the modified second part
//            String result = firstPart + "-" + secondPart;
//            contentStream.showText(result);
//        }

        contentStream.newLineAtOffset(350, 56);

        contentStream.newLineAtOffset(-78, -147);
        contentStream.showText(String.valueOf(registrationAdvance.getRegistrationFee()).toUpperCase() + ".00");
        contentStream.newLineAtOffset(0, -26);
        contentStream.showText(String.valueOf(info.getCourseFee()).toUpperCase() + ".00");
        contentStream.newLineAtOffset(120, 26);
        contentStream.showText(String.valueOf(registrationAdvance.getRegistrationFee()).toUpperCase() + ".00");
        contentStream.newLineAtOffset(0, -26);
        contentStream.showText(String.valueOf(info.getCourseFee()).toUpperCase() + ".00");
        contentStream.newLineAtOffset(0, -103);
        contentStream.showText(String.valueOf(info.getCourseFee() + info.getRegistrationFee()).toUpperCase() + ".00");
        contentStream.newLineAtOffset(-19, 320);

        // Handling Intake ID
        String intakeId = info.getIntakeId();
        String[] parts = intakeId.split("-");
        contentStream.newLineAtOffset(-345, -422);

        if (parts.length == 2) {
            String firstPart = parts[0]; // First part before the hyphen
            String secondPart = parts[1]; // Second part after the hyphen

            // Keep only the first two characters of the second part
            if (secondPart.length() > 2) {
                secondPart = secondPart.substring(0, 2);
            }

            // Concatenate the first part with the modified second part
            String result = firstPart + "-" + secondPart;
            contentStream.showText(result);
        }

        contentStream.newLineAtOffset(0, -17);
        contentStream.showText(formattedDate.toUpperCase());
        contentStream.newLineAtOffset(319, 399);

        if (parts.length == 2) {
            String firstPart = parts[0]; // First part before the hyphen
            String secondPart = parts[1]; // Second part after the hyphen

            // Keep only the first two characters of the second part
            if (secondPart.length() > 2) {
                secondPart = secondPart.substring(0, 2);
            }

            // Concatenate the first part with the modified second part
            String result = firstPart + "-" + secondPart;
            contentStream.showText("JA" + result + "-" + String.valueOf(info.getId()).toUpperCase());
        }

        contentStream.newLineAtOffset(0, -16);
        contentStream.showText(formattedDate.toUpperCase());
        contentStream.newLineAtOffset(0, -15);

        contentStream.showText("CD");
        contentStream.setNonStrokingColor(0, 0, 0);

        contentStream.newLineAtOffset(-345, -36);

        if (parts.length == 2) {
            String firstPart = parts[0];
            String secondPart = parts[1].length() > 2 ? parts[1].substring(0, 2) : parts[1];
            String result = firstPart + "-" + secondPart;

            contentStream.showText(result);
        }

        contentStream.endText();

        contentStream.close();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();

        return outputStream;
    }



    @Transactional
    public RegistrationAdvance addCourseFeeDetailsOffline(Long registrationId, CourseFeeDetailsDTO feeDetailsDTO) {
        Optional<RegistrationAdvance> optionalRegistrationAdvance = registrationAdvanceRepository.findById(registrationId);
        if (!optionalRegistrationAdvance.isPresent()) {
            throw new RuntimeException("RegistrationAdvance not found with id " + registrationId);
        }

        RegistrationAdvance registrationAdvance = optionalRegistrationAdvance.get();
        CourseFeeDetails feeDetail = new CourseFeeDetails();
        feeDetail.setSessionId(feeDetailsDTO.getSessionId());
        feeDetail.setSessionDate(LocalDate.now());
        feeDetail.setAmount(feeDetailsDTO.getAmount());
        feeDetail.setPaymentMode(feeDetailsDTO.getPaymentMode());
        feeDetail.setRemarks(feeDetailsDTO.getRemarks());
        feeDetail.setStatus("Paid");


        registrationAdvance.getCourseFeeDetails().add(feeDetail);
        return registrationAdvanceRepository.save(registrationAdvance);
    }



    public List<RegistrationAdvance> getRegistrationAdvances(String approveReference) {
        // Always check if the status is "new" first
        if (approveReference != null && !approveReference.isEmpty()) {
            // If approveReference is provided, find records where status is "new" and approveReference matches or is empty
            return registrationAdvanceRepository.findByStatusAndApproveReferenceOrApproveReferenceIsEmpty(approveReference);

        } else {
            // If approveReference is null or empty, return records where status is "new"
//            return registrationAdvanceRepository.findByStatus("new");
            return registrationAdvanceRepository.findByStatusWithNullOrEmptyApproveReference("new");
        }
    }
    public List<RegistrationAdvance> getRegistrationAdvancesForNewOrRegister(String approveReference) {
        // Fetch records with status 'new' or 'register', and approveReference either matching or being NULL/empty
        return registrationAdvanceRepository.findByStatusNewOrRegisterAndApproveReferenceOrApproveReferenceIsEmpty(approveReference);
    }
    public List<RegistrationAdvance> getAllRegistrationAdvancesByBatchId(Long batchId) {
        return registrationAdvanceRepository.findByBatchId(batchId);
    }

    @Transactional
    public void sendPaymentReceipts(Long id) throws MessagingException, IOException, URISyntaxException, InterruptedException {
        RegistrationAdvance registrationAdvance = getRegistrationAdvanceById(id);
        if (registrationAdvance != null) {
            List<CourseFeeDetails> courseFeeDetailsList = registrationAdvance.getCourseFeeDetails();

            // Check if the list is not empty
            if (!courseFeeDetailsList.isEmpty()) {
                // Get the last CourseFeeDetails
                CourseFeeDetails lastCourseFeeDetail = courseFeeDetailsList.get(courseFeeDetailsList.size() - 1);

                // Check if the email has been sent for the last payment
                if (lastCourseFeeDetail.isEmailCourseSent()) {
                    System.out.println("Email has already been sent for the last payment. Skipping email sending process.");
                    return; // Exit the method if the email was already sent for the last payment
                }
            }

            // Continue with the rest of your logic...
        }

        if (registrationAdvance != null) {
            // Retrieve the list of CourseFeeDetails
            List<CourseFeeDetails> courseFeeDetailsList = registrationAdvance.getCourseFeeDetails();

            // Check if the list is not empty and get the last CourseFeeDetails
            if (!courseFeeDetailsList.isEmpty()) {
                CourseFeeDetails lastCourseFeeDetail = courseFeeDetailsList.get(courseFeeDetailsList.size() - 1);

                // Optionally skip email sending process based on lastCourseFeeDetail (commented out for now)
            }

            // Populate RegistrationAdvanceInfo
            RegistrationAdvanceInfo info = populateRegistrationAdvanceInfo(registrationAdvance);

            // Fetch email and phone number from registrationAdvance
            String email = registrationAdvance.getEmail();
            String phoneNumber = registrationAdvance.getHandPhone().replace(" ", ""); // Remove spaces from phone number

            // Default subject and text for regular payment
            String subject = "Your Payment Receipt";
            String text = "Thank you for your payment. Here is your receipt.";

            // Find the last installment with feeType "Course Fee"
            CourseFeeDetails lastCourseFeeInstallment = null;
            for (CourseFeeDetails installmentDetails : info.getCourseFeeDetails()) {
                if ("CourseFee".equals(installmentDetails.getType())) {
                    lastCourseFeeInstallment = installmentDetails;
                }
            }

            // Check if the last installment exists and proceed with email sending
            if (lastCourseFeeInstallment != null) {
                // Safely get the installmentCounts array list from the last paid CourseFee installment
                List<Integer> installmentCountsList = lastCourseFeeInstallment.getInstallmentCounts();

                // Check and handle possible null values in installmentCountsList
                if (installmentCountsList != null) {
                    System.out.println("Installment Counts: " + installmentCountsList);
                } else {
                    System.out.println("No installment counts available.");
                }

                sendInvoiceEmails(email, subject, text, info, lastCourseFeeInstallment, registrationAdvance);
                sendWhatsAppMessage(phoneNumber, lastCourseFeeInstallment, info, registrationAdvance);
                for (CourseFeeDetails feeDetails : courseFeeDetailsList) {
                    feeDetails.setEmailCourseSent(true);
                }
                // Set the emailSent flag to true to prevent resending
                lastCourseFeeInstallment.setEmailCourseSent(true);

                // Persist the change to the CourseFeeDetails in the RegistrationAdvance
                registrationAdvanceRepository.save(registrationAdvance);

                // Mark email as sent in the RegistrationAdvance entity
                registrationAdvance.setEmailCourseSent(true);
            } else {
                System.out.println("Email has already been sent for this installment. Skipping email sending process.");
            }

            // Safely set the sequence number from the last installment in the RegistrationAdvance entity
            if (lastCourseFeeInstallment != null ) {
                int sequenceNumber = lastCourseFeeInstallment.getSequenceNumber();
                registrationAdvance.setSequenceNumber(sequenceNumber);
            } else {
                // Set a default sequence number if it is not generated
                registrationAdvance.setSequenceNumber(0);
            }

            // Mark the final payment as confirmed if all fees are paid
            if (info.getRegistrationFeeRemaining() == 0 && info.getCourseFeeRemaining() == 0) {
                registrationAdvance.setFinalPaymentConfirmed(true);
            }
            updateStatuses(registrationAdvance);

            // Update registrationAdvance in the database
            registrationAdvanceRepository.save(registrationAdvance);
        }
    }


    private ByteArrayOutputStream sendInvoiceEmails(String to, String subject, String text, RegistrationAdvanceInfo info, CourseFeeDetails installmentDetails, RegistrationAdvance registrationAdvance) throws MessagingException, IOException {
        List<ScheduleDate> scheduleDates = registrationAdvance.getScheduleDate();
        RegistrationAdvancesDetailsDTO dto = convertToDTO(registrationAdvance, scheduleDates);

        // Obtain the intakeId from the DTO
        String intakeId = dto.getIntakeId();
        ClassPathResource pdfResource = new ClassPathResource("instalment-receipt.pdf");
        ByteArrayOutputStream outputStream = modifyPdfs(pdfResource.getInputStream(), info, installmentDetails, intakeId);

        // Generate the file name using the student's name and NRIC number
        String publicId = registrationAdvance.getName() + "_" + registrationAdvance.getNric();

        try {
            // Upload PDF to Cloudinary with public ID
            String pdfUrl = uploadPdfToCloudinaryAndGetUrl(outputStream.toByteArray(), publicId);

            // Create and send email with attachment
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text + "\n\nYou can download your receipt here: " + pdfUrl);

            InputStreamSource attachment = new ByteArrayResource(outputStream.toByteArray());
            helper.addAttachment(publicId + ".pdf", attachment);

            mailSender.send(message);

            return outputStream;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            outputStream.close();
        }

        return outputStream;
    }

    public void sendWhatsAppMessage(String phoneNumber, CourseFeeDetails installmentDetails, RegistrationAdvanceInfo info, RegistrationAdvance registrationAdvance) throws IOException, URISyntaxException, InterruptedException {
        String apiKey = "c4a984a808414ef8af95bf72658d8463";
        String url = "https://api.bulkwhatsapp.net/wapp/api/send";

        // Generate the file name using the student's name and NRIC number
        String publicId = registrationAdvance.getName() + "_" + registrationAdvance.getNric();

        // Generate intakeId
        String intakeId = info.getIntakeId();

        // Generate the PDF bytes
        byte[] pdfBytes = generatePdfBytes(info, installmentDetails, intakeId);

        // Upload PDF to Cloudinary with public ID and get URL
        String pdfUrl = uploadPdfToCloudinaryAndGetUrl(pdfBytes, publicId);

        // Message content
        String messageContent = "Hi Mr. " + registrationAdvance.getName() + ",\n" +
                "You are studying " + registrationAdvance.getBatch().getType() + " Course in our Jurong Academy.\n" +
                "Your balance Course Fee Amount is " + info.getCourseFeeRemaining() + ".\n" +
                "Thank you for your payment.";

        // Construct the URI for WhatsApp message
        URI uri = new URI(url + "?apikey=" + apiKey + "&mobile=" + phoneNumber + "&msg=" + URLEncoder.encode(messageContent, StandardCharsets.UTF_8.toString()) + "&pdf=" + URLEncoder.encode(pdfUrl, StandardCharsets.UTF_8.toString()));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to send WhatsApp message: " + response.body());
        }
    }

    private byte[] generatePdfBytes(RegistrationAdvanceInfo info, CourseFeeDetails installmentDetails, String intakeId) throws IOException {
        try (ByteArrayOutputStream outputStream = modifyPdfs(
                new ClassPathResource("instalment-receipt.pdf").getInputStream(),
                info,
                installmentDetails,
                intakeId)) {
            return outputStream.toByteArray();
        }
    }

    private static String generateInvoiceNumbers(Long registrationId) {
        AtomicLong counter = invoiceCounters.computeIfAbsent(registrationId, k -> new AtomicLong(0));
        long invoiceNumber = counter.incrementAndGet() % 10;
        return String.valueOf(invoiceNumber);
    }


    public static ByteArrayOutputStream modifyPdfs(InputStream pdfInvoice, RegistrationAdvanceInfo info, CourseFeeDetails installmentDetails, String intakeId) throws IOException {
        // Use the stored sequence number from CourseFeeDetails
        int sequenceNumber = info.getSequenceNumber();
        System.out.println("Retrieved Sequence Number: " + sequenceNumber);
        String formattedSequence = String.format("%04d", sequenceNumber); // Format the sequence number with leading zeros
        String invoiceNumber = "JAO/CF-24-" + formattedSequence; // Construct the invoice number
        System.out.println("Invoice Number: " + invoiceNumber);

        PDDocument document = PDDocument.load(pdfInvoice);
        PDPage page = document.getPage(0);
        InputStream fontStream = RegistrationAdvanceService.class.getResourceAsStream("/calibri.ttf");
        PDType0Font calibri = PDType0Font.load(document, fontStream);

        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
        String courseName = info.getCourse().replaceAll("\\(.*?\\)", "").trim();

        // Convert the date format
        String dateToUse = info.isLate() ? String.valueOf(info.getScheduleDate()) : String.valueOf(info.getDate());
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(dateToUse, inputFormatter);
        String formattedDate = date.format(outputFormatter);

        // Calculate GST amount
        double amount = installmentDetails.getAmount();
        double amountExcludingGst = amount / 1.09;
        double gstAmount = amount - amountExcludingGst;

        contentStream.beginText();
        contentStream.setFont(calibri, 10);
        contentStream.setNonStrokingColor(0, 0, 0);

        BiConsumer<PDPageContentStream, String> showTextSafe = (stream, text) -> {
            try {
                if (text != null) {
                    stream.showText(text);
                } else {
                    stream.showText("");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        // Populate the content stream
        contentStream.newLineAtOffset(200, 618);
        showTextSafe.accept(contentStream, info.getName());
        contentStream.newLineAtOffset(0, -11);
        showTextSafe.accept(contentStream, info.getNric());
        contentStream.newLineAtOffset(0, -11);
        showTextSafe.accept(contentStream, info.getCourse());
        contentStream.newLineAtOffset(0, -11);
        showTextSafe.accept(contentStream, info.getIntakeId());
        contentStream.newLineAtOffset(-12, -55);
        showTextSafe.accept(contentStream, String.format("%.2f", gstAmount));
        contentStream.newLineAtOffset(-30, -22);
        showTextSafe.accept(contentStream, String.valueOf(info.getCourseFeeRemaining()));

        // Add installment amount
        contentStream.newLineAtOffset(320, 50);
        showTextSafe.accept(contentStream, String.valueOf("$" + installmentDetails.getAmount()));

        contentStream.newLineAtOffset(5, -84);
        showTextSafe.accept(contentStream, String.valueOf(installmentDetails.getAmount()));
        contentStream.newLineAtOffset(-345, -25);
        showTextSafe.accept(contentStream, info.getApprover());
        contentStream.newLineAtOffset(315, 195);
        showTextSafe.accept(contentStream, invoiceNumber);
        contentStream.newLineAtOffset(-66, -14);
        showTextSafe.accept(contentStream, formattedDate);
        contentStream.newLineAtOffset(-241, -156);
        showTextSafe.accept(contentStream, String.valueOf(convertToWords(Math.toIntExact(installmentDetails.getAmount())).toUpperCase()) + " DOLLARS ONLY");

        // Handle installment range (e.g., "1st-3rd installment")
        // Handle installment counts
        ArrayList<Integer> installmentCounts = installmentDetails.getInstallmentCounts();
        if (!installmentCounts.isEmpty()) {
            if (installmentCounts.size() == 1) {
                // Single installment case
                int singleInstallment = installmentCounts.get(0);
                String singleSuffix = getInstallmentSuffix(singleInstallment);
                String singleInstallmentText = String.format("%d%s installment", singleInstallment, singleSuffix);

                // Display the single installment and amount
                contentStream.newLineAtOffset(-68, 66);
                showTextSafe.accept(contentStream, singleInstallmentText + " $" + installmentDetails.getAmount());
            } else {
                // Multiple installments case (display range)
                int firstInstallment = installmentCounts.get(0);
                int lastInstallment = installmentCounts.get(installmentCounts.size() - 1);

                // Check if first and last installments are the same
                if (firstInstallment == lastInstallment) {
                    // Display single installment
                    String singleSuffix = getInstallmentSuffix(firstInstallment);
                    String singleInstallmentText = String.format("%d%s installment", firstInstallment, singleSuffix);
                    contentStream.newLineAtOffset(-68, 66);
                    showTextSafe.accept(contentStream, singleInstallmentText + " $" + installmentDetails.getAmount());
                } else {
                    // Display range of installments
                    String firstSuffix = getInstallmentSuffix(firstInstallment);
                    String lastSuffix = getInstallmentSuffix(lastInstallment);
                    String installmentText = String.format("%d%s-%d%s installments", firstInstallment, firstSuffix, lastInstallment, lastSuffix);

                    // Display the installment range and amount
                    contentStream.newLineAtOffset(-68, 66);
                    showTextSafe.accept(contentStream, installmentText + " $" + installmentDetails.getAmount());
                }
            }
        }

        contentStream.endText();
        contentStream.close();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();
        return outputStream;
    }

    // Helper function to get the correct suffix (st, nd, rd, th) for installment numbers
    private static String getInstallmentSuffix(int number) {
        if (number % 100 >= 11 && number % 100 <= 13) {
            return "th";
        }
        switch (number % 10) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }




    public void sendPaymentReceiptEmail(Long id) throws MessagingException, IOException, URISyntaxException, InterruptedException {
        RegistrationAdvance registrationAdvance = getRegistrationAdvanceById(id);
        if (registrationAdvance != null) {
            // Populate RegistrationAdvanceInfo
            RegistrationAdvanceInfo info = populateRegistrationAdvanceInfo(registrationAdvance);
            // Fetch email and phone number from registrationAdvance
            String email = registrationAdvance.getEmail();
            String phoneNumber = registrationAdvance.getHandPhone().replace(" ", ""); // Remove spaces from phone number

            // Default subject and text for regular payment
            String subject = "Your Payment Receipt";
            String text = "Thank you for your payment. Here is your receipt.";

            // Iterate through each installment to send separate emails
            List<CourseFeeDetails> installments = registrationAdvance.getCourseFeeDetails();
            for (CourseFeeDetails installmentDetails : installments) {
                if (installmentDetails.isEmailSent()) {
                    System.out.println("Email is sent already");
                    return;
                }
               // installmentDetails.setEmailSent(true);
                sendInvoiceEmail(email, subject, text, info, installmentDetails, registrationAdvance);

                // Send WhatsApp message
                sendWhatsAppForRegistrationMessage(phoneNumber, installmentDetails, info, registrationAdvance);
            }

            // Mark email as sent in the RegistrationAdvance entity
           // registrationAdvance.setEmailSent(true);
//            // Mark the final payment as confirmed if all fees are paid
//            if (info.getRegistrationFeeRemaining() == 0 && info.getCourseFeeRemaining() == 0) {
//                registrationAdvance.setFinalPaymentConfirmed(true);
//            }
            updateStatuses(registrationAdvance);

            // Update registrationAdvance in the database
            registrationAdvanceRepository.save(registrationAdvance);
        }
    }
    public void updateStatuses(RegistrationAdvance registrationAdvance) {
        // Update the status for RegistrationAdvance
        registrationAdvance.setStatus("active");

        // Update the status for the associated Student
        Student student = registrationAdvance.getStudent();
        if (student != null) {
            student.setStatus("active");
            studentRepository.save(student);
        }

        // Update the status for the associated User entity using sfid
        Long sfid = registrationAdvance.getId();
        Optional<User> optionalUser = userRepository.findBySfidAndUsertype(sfid,1);

        if (optionalUser == null) {
            throw new IllegalStateException("OurUsersRepository returned null for sfid: " + sfid);
        }

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setStatus("active");
            userRepository.save(user);
        } else {
            // Add logging to check if sfid exists in the database
            System.out.println("No user found with sfid: " + sfid);
        }
    }

    private CourseFeeDetails getLastPaidInstallment(RegistrationAdvance registrationAdvance) {
        List<CourseFeeDetails> courseFeeDetails = registrationAdvance.getCourseFeeDetails();
        return courseFeeDetails.stream()
                .filter(details -> "paid".equals(details.getStatus()))
                .reduce((first, second) -> second)
                .orElse(null);
    }

    //changes done here for registration-reciept pdf email
    private static final String[] units = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
    private static final String[] teens = {"Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
    private static final String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

    public static String convertToWords(int number) {
        if (number == 0) {
            return "zero";
        }

        if (number < 0) {
            return "minus " + convertToWords(-number);
        }

        String words = "";

        if ((number / 1000000) > 0) {
            words += convertToWords(number / 1000000) + " Million ";
            number %= 1000000;
        }

        if ((number / 1000) > 0) {
            words += convertToWords(number / 1000) + " Thousand ";
            number %= 1000;
        }

        if ((number / 100) > 0) {
            words += convertToWords(number / 100) + " Hundred ";
            number %= 100;
        }

        if (number > 0) {
            if (number < 10) {
                words += units[number];
            } else if (number < 20) {
                words += teens[number - 10];
            } else {
                words += tens[number / 10];
                if ((number % 10) > 0) {
                    words += " " + units[number % 10];
                }
            }
        }

        return words.trim();
    }

    //changes done here for registration-reciept pdf email
    public static ByteArrayOutputStream modifyPdfss(InputStream pdfInvoice, RegistrationAdvanceInfo info, CourseFeeDetails installmentDetails, String intakeId, List<ScheduleDate> scheduleDates) throws IOException {
        String invoicePrefix = "JA";

        // Extract overall start date from the scheduleDates list
        LocalDate overallStartDate = null;
        if (scheduleDates != null && !scheduleDates.isEmpty()) {
            overallStartDate = scheduleDates.get(0).getStartDate();
        }

        PDDocument document = PDDocument.load(pdfInvoice);
        PDPage page = document.getPage(0);
        InputStream fontStream = RegistrationAdvanceService.class.getResourceAsStream("/calibribold.ttf");
        InputStream fontStream1 = RegistrationAdvanceService.class.getResourceAsStream("/calibri.ttf");
        PDType0Font calibribold = PDType0Font.load(document, fontStream);
        PDType0Font calibriregular = PDType0Font.load(document, fontStream1);

        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
        String courseName = info.getCourse().replaceAll("\\(.*?\\)", "").trim();

        // Convert the date format
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(info.getDate(), inputFormatter);
        String formattedDate;
        if (info.isLate() && installmentDetails.getInstallmentCount() == 1 && overallStartDate != null) {
            formattedDate = overallStartDate.format(outputFormatter);
        } else {
            formattedDate = date.format(outputFormatter);
        }


        // Extract the last two digits of the year
        String yearLastTwoDigits = String.valueOf(date.getYear()).substring(2);

        // Combine the invoice prefix with the year last two digits
        String invoiceNumber = invoicePrefix + yearLastTwoDigits + "-" + info.getId() + "-" + info.getNric();

        // Calculate GST amount
        double amount = installmentDetails.getAmount();
        double amountExcludingGst = amount / 1.09;
        double gstAmount = amount - amountExcludingGst;

        contentStream.beginText();
        contentStream.setFont(calibribold, 10);
        contentStream.setNonStrokingColor(0, 0, 0);
        BiConsumer<PDPageContentStream, String> showTextSafe = (stream, text) -> {
            try {
                if (text != null) {
                    stream.showText(text);
                } else {
                    stream.showText("");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        contentStream.newLineAtOffset(200, 642);
        showTextSafe.accept(contentStream, info.getName().toUpperCase());

        contentStream.newLineAtOffset(0, -13);     // Move up for NRIC
        showTextSafe.accept(contentStream, info.getNric());

        contentStream.newLineAtOffset(0, -11);     // Move up for Course
        showTextSafe.accept(contentStream, courseName.toUpperCase());
        contentStream.newLineAtOffset(0, -13);     // Move up for Intake ID
        showTextSafe.accept(contentStream, intakeId);  // Directly use intakeId here

        contentStream.newLineAtOffset(185, 61);   // Move up and right for Date (top right)
        showTextSafe.accept(contentStream, formattedDate);
        contentStream.newLineAtOffset(25, 12);    // Move up for Invoice Number
        showTextSafe.accept(contentStream, invoiceNumber);
        contentStream.newLineAtOffset(-265, -123);  // Move up and left for Installment Amount
        contentStream.setFont(calibriregular, 10);
        showTextSafe.accept(contentStream, String.valueOf(amount) + "(OMT)");
        contentStream.newLineAtOffset(10, -11);     // Move up for GST Amount
        contentStream.setFont(calibribold, 10);
        showTextSafe.accept(contentStream, "$" + String.format("%.2f", gstAmount) + ")");
        contentStream.newLineAtOffset(-45, -22);   // Move up and left for Registration Fee Remaining
        contentStream.setFont(calibriregular, 10);
        showTextSafe.accept(contentStream, String.valueOf(info.getRegistrationFeeRemaining()));
        contentStream.newLineAtOffset(365, 45);  // Move up and left for Installment Amount
        showTextSafe.accept(contentStream, "$" + String.valueOf(amount));
        contentStream.newLineAtOffset(0, -75);  // Move up and left for Installment Amount
        contentStream.setFont(calibribold, 10);
        showTextSafe.accept(contentStream, "$" + String.valueOf(amount));

        contentStream.newLineAtOffset(-375, -13);   // Move up and left for Registration Fee Remaining
        contentStream.setFont(calibriregular, 10);
//        showTextSafe.accept(contentStream, String.valueOf(info.getApprover()).toUpperCase());
        contentStream.newLineAtOffset(30, 15);  // Move up and left for Installment Amount

        showTextSafe.accept(contentStream, String.valueOf(convertToWords(Math.toIntExact((long) amount)).toUpperCase()) + " DOLLARS ONLY");
//        contentStream.newLineAtOffset(-59, 40);
//        int installmentCount = installmentDetails.getInstallmentCount();
//
//        String installmentText = "$" + String.valueOf(amount);
//        String suffix;
//        switch (installmentCount) {
//            case 1:
//                suffix = installmentCount + "st installment amount";
//                break;
//            case 2:
//                suffix = installmentCount + "nd installment amount";
//                break;
//            case 3:
//                suffix = installmentCount + "rd installment amount";
//                break;
//            default:
//                suffix = installmentCount + "th installment amount";
//                break;
//        }
//        showTextSafe.accept(contentStream, suffix + " installment" + installmentText);

        contentStream.endText();
        contentStream.close();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();
        return outputStream;
    }
    private ByteArrayOutputStream sendInvoiceEmail(String to, String subject, String text, RegistrationAdvanceInfo info, CourseFeeDetails installmentDetails, RegistrationAdvance registrationAdvance) throws MessagingException, IOException {
        // Convert registration and schedule dates to DTO
        List<ScheduleDate> scheduleDates = registrationAdvance.getScheduleDate();
      RegistrationAdvancesDetailsDTO dto = convertToDTO(registrationAdvance, scheduleDates);
//
        // Obtain the intakeId from the DTO
       String intakeId = dto.getIntakeId();

        // Prepare the PDF
        ClassPathResource pdfResource = new ClassPathResource("registration-reciept-new.pdf");
        ByteArrayOutputStream outputStream = modifyPdfss(pdfResource.getInputStream(), info, installmentDetails, intakeId, scheduleDates);

        // Generate the file name using the student's name and NRIC number
        String publicId = registrationAdvance.getName() + "_" + registrationAdvance.getNric();

        try {
            // Upload PDF to Cloudinary with public ID
            String pdfUrl = uploadPdfToCloudinaryAndGetUrl(outputStream.toByteArray(), publicId);

            // Create and send the first email with attachment to the recipient (student)
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text + "\n\nYou can download your receipt here: " + pdfUrl);

            InputStreamSource attachment = new ByteArrayResource(outputStream.toByteArray());
            helper.addAttachment(publicId + ".pdf", attachment);

            mailSender.send(message);

            // Log first email sent
            System.out.println("First email sent successfully to " + to);

            // Prepare and send the additional email to "bharathkrishnanbsc@gmail.com"
            // Prepare and send the additional email to "bharathkrishnanbsc@gmail.com"
            MimeMessage secondMessage = mailSender.createMimeMessage();
            MimeMessageHelper secondHelper = new MimeMessageHelper(secondMessage, true);
            secondHelper.setTo("bharathkrishnanbsc@gmail.com");
            secondHelper.setSubject("Payment Receipt Notification");

// Compose the content for the second email
            String secondEmailContent = String.format(
                    "Student Name: %s\nNRIC: %s\nIntake ID: %s\nAmount: %d", // Change %.2f to %d for Long type
                    registrationAdvance.getName(),
                    registrationAdvance.getNric(),
                    registrationAdvance.getIntakeId(),
                    installmentDetails.getAmount() // Assuming this returns a Long
            );

            secondHelper.setText(secondEmailContent);
            mailSender.send(secondMessage);

            // Log second email sent
            System.out.println("Second email sent successfully to bharathkrishnanbsc@gmail.com");

        } catch (Exception e) {
            // Log the exception in case of failure
            System.err.println("Failed to send emails: " + e.getMessage());
            e.printStackTrace();
        } finally {
            outputStream.close();
        }

        // Return outputStream for further use
        return outputStream;
    }
    public void sendWhatsAppForRegistrationMessage(String phoneNumber, CourseFeeDetails installmentDetails, RegistrationAdvanceInfo info, RegistrationAdvance registrationAdvance) throws IOException, URISyntaxException, InterruptedException {
        String apiKey = "c4a984a808414ef8af95bf72658d8463";
        String url = "https://api.bulkwhatsapp.net/wapp/api/send";

        // Generate the file name using the student's name and NRIC number
        String publicId = registrationAdvance.getName() + "_" + registrationAdvance.getNric();

        // Generate intakeId
        String intakeId = info.getIntakeId(); // Make sure info has the intakeId set correctly
        List<ScheduleDate> scheduleDates = registrationAdvance.getScheduleDate();
        // Generate the PDF bytes
        byte[] pdfBytes = generatePdfBytess(info, installmentDetails, intakeId,scheduleDates);

        // Upload PDF to Cloudinary with public ID and get URL
        String pdfUrl = uploadPdfToCloudinaryAndGetUrl(pdfBytes, publicId);

        // Message content
        String messageContent = "Hi Mr. " + registrationAdvance.getName() + ",\n" +
                "You are studying " + registrationAdvance.getBatch().getType() + " Course in our Jurong Academy.\n" +
                "Your balance Course Fee Amount is " + info.getRegistrationFeeRemaining() + ".\n" +
                "Thank you for your payment.";

        // Construct the URI for WhatsApp message
        URI uri = new URI(url + "?apikey=" + apiKey + "&mobile=" + phoneNumber + "&msg=" + URLEncoder.encode(messageContent, StandardCharsets.UTF_8.toString()) + "&pdf=" + URLEncoder.encode(pdfUrl, StandardCharsets.UTF_8.toString()));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to send WhatsApp message: " + response.body());
        }
    }

    private byte[] generatePdfBytess(RegistrationAdvanceInfo info, CourseFeeDetails installmentDetails, String intakeId, List<ScheduleDate> scheduleDates) throws IOException {
        try (ByteArrayOutputStream outputStream = modifyPdfss(
                new ClassPathResource("registration-reciept-new.pdf").getInputStream(),
                info,
                installmentDetails,
                intakeId,
                scheduleDates)) {
            return outputStream.toByteArray();
        }
    }


    public String findStudentStatusByNric(String nric) {
        Optional<Student> student = studentRepository.findByNric(nric);
        return student.map(Student::getStatus).orElse(null);
    }

//    @Transactional
//    public void deleteRegistrationAdvanceByAlumniStatus() {
//        org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
//
//        // Fetch all students with status "Alumni"
//        List<Student> alumniStudents = studentRepository.findByStatus("Alumni");
//        logger.info("Found {} students with status 'Alumni'", alumniStudents.size());
//
//        // Iterate over each alumni student
//        for (Student alumniStudent : alumniStudents) {
//            Long studentId = alumniStudent.getId();
//            List<RegistrationAdvance> registrationAdvances = registrationAdvanceRepository.findByStudentId(studentId);
//            logger.info("Found {} registration advances for student with ID {}", registrationAdvances.size(), studentId);
//
//            for (RegistrationAdvance registrationAdvance : registrationAdvances) {
//                // Calculate registration fee balance and course fee balance
//                RegistrationAdvanceInfo info = populateRegistrationAdvanceInfo(registrationAdvance);
//                long registrationFeeRemaining = info.getRegistrationFeeRemaining();
//                long courseFeeRemaining = info.getCourseFeeRemaining();
//
//                if (registrationFeeRemaining == 0 && courseFeeRemaining == 0) {
//                    Garbage garbage = new Garbage();
//                    // Copy properties from RegistrationAdvance to Garbage
//                    garbage.setRegistrationAdvanceId(registrationAdvance.getId());
//                    garbage.setStudentId(registrationAdvance.getStudent().getId());
//                    garbage.setType(registrationAdvance.getType());
//                    garbage.setTitle(registrationAdvance.getTitle());
//                    garbage.setIntake(registrationAdvance.getIntake());
//                    garbage.setName(registrationAdvance.getName());
//
//
//                    garbage.setCountryBirth(registrationAdvance.getCountryBirth());
//                    garbage.setDob(registrationAdvance.getDob().toString()); // assuming dob is a Date
//                    garbage.setGender(registrationAdvance.getGender());
//                    garbage.setNationality(registrationAdvance.getNationality());
//                    garbage.setNric(registrationAdvance.getNric());
//                    garbage.setPassportNo(registrationAdvance.getPassportNo());
//                    garbage.setDesignation(registrationAdvance.getDesignation());
//                    garbage.setAddressOffice(registrationAdvance.getAddressOffice());
//                    garbage.setAddressHome(registrationAdvance.getAddressHome());
//                    garbage.setPhoneOffice(registrationAdvance.getPhoneOffice());
//                    garbage.setPhoneHome(registrationAdvance.getPhoneHome());
//                    garbage.setHandPhone(registrationAdvance.getHandPhone());
//                    garbage.setEmail(registrationAdvance.getEmail());
//                    garbage.setEmergencyName(registrationAdvance.getEmergencyName());
//                    garbage.setEmergencyRelation(registrationAdvance.getEmergencyRelation());
//                    garbage.setEmergencyPhone(registrationAdvance.getEmergencyPhone());
//                    garbage.setKnowAboutProgram(registrationAdvance.getKnowAboutProgram());
//                    garbage.setApplicationSignature(registrationAdvance.getApplicationSignature());
//                    garbage.setDate(registrationAdvance.getDate()); // assuming date is a Date
//                    garbage.setCompany(registrationAdvance.getCompany());
//                    // garbage.setPhoto(registrationAdvance.getPhoto());
//                    garbage.setApprove(registrationAdvance.getApprove());
//                    //garbage.setApplicationForm(registrationAdvance.getApplicationForm());
//                    // garbage.setWorkPermit(registrationAdvance.getWorkPermit());
//                    // garbage.setAcademicCertificates(registrationAdvance.getAcademicCertificates());
//                    //garbage.setAcademicTranscripts(registrationAdvance.getAcademicTranscripts());
//                    //garbage.setOthers(registrationAdvance.getOthers());
//                    //garbage.setSessionId(registrationAdvance.getSessionId());
//                    //garbage.setSessionIdDate(registrationAdvance.getSessionIdDate().toString()); // assuming sessionIdDate is a Date
//                    garbage.setAmount(registrationAdvance.getAmount());
//                    garbage.setPaymentStatus(registrationAdvance.getPaymentStatus());
//                    garbage.setPaymentType(registrationAdvance.getPaymentType());
//                    garbage.setInstitutionDetails(registrationAdvance.getInstitutionDetails());
//                    garbage.setCourseFeeDetails(registrationAdvance.getCourseFeeDetails());
//                    garbage.setScheduleDate(registrationAdvance.getScheduleDate());
//                    garbage.setBatch(registrationAdvance.getBatch());
//                    garbage.setStartModulus(registrationAdvance.getStartModulus());
//                    garbage.setEndModulus(registrationAdvance.getEndModulus());
//                    garbage.setEnrolledDate(registrationAdvance.getEnrolledDate());
//                    //garbage.setGstAmount(registrationAdvance.getGstAmount());
//                    //garbage.setTotalAmount(registrationAdvance.getTotalAmount());
//                    //garbage.setEmiAmount(registrationAdvance.getEmiAmount());
//                    //garbage.setEmiMonths(registrationAdvance.getEmiMonths());
//                    //garbage.setPaymentDate(registrationAdvance.getPaymentDate());
//                    garbage.setStatus(registrationAdvance.getStudent().getStatus());
//
//                    // Save the Garbage entity
//                    garbageRepository.save(garbage);
//                    logger.info("Saved garbage record for registration advance with ID {}", registrationAdvance.getId());
//
//                    // Delete the RegistrationAdvance entity
//                    registrationAdvanceRepository.delete(registrationAdvance);
//                    logger.info("Deleted registration advance with ID {}", registrationAdvance.getId());
//                }
//
//                // Delete the alumni student after processing their RegistrationAdvance entities
//                studentRepository.delete(alumniStudent);
//                logger.info("Deleted student with ID {}", studentId);
//            }
//        }
//    }

    public List<Garbage> getGarbageByIntakeId(String intakeId) {
        return garbageRepository.findByIntakeId(intakeId);
    }
    public RegistrationAdvance findById(Long id) {
        return registrationAdvanceRepository.findById(id).orElse(null);
    }
    // Transfer method
    public void transferAlumniToGarbage() {
        // Fetch the list of RegistrationAdvance entities where status is "Alumni" and remaining fees are 0
        List<RegistrationAdvance> alumniRegistrations = registrationAdvanceRepository.findByStatusAndFeesRemaining("Alumni");

        // Loop through each registrationAdvance and transfer it
        for (RegistrationAdvance registrationAdvance : alumniRegistrations) {

            // Create a new Garbage entity and set all fields from RegistrationAdvance
            Garbage garbage = new Garbage();
            garbage.setRegistrationAdvanceId(registrationAdvance.getId());
            garbage.setStudentId(registrationAdvance.getStudent().getId());
            garbage.setType(registrationAdvance.getType());
            garbage.setTitle(registrationAdvance.getTitle());
            garbage.setIntakeId(registrationAdvance.getIntakeId());
            garbage.setName(registrationAdvance.getStudent().getName());
            // garbage.setSureName(registrationAdvance.getStudent().getFullName());
            garbage.setCountryBirth(registrationAdvance.getCountryBirth());
            garbage.setDob(registrationAdvance.getDob());
            garbage.setGender(registrationAdvance.getGender());
            garbage.setNationality(registrationAdvance.getNationality());
            garbage.setNric(registrationAdvance.getNric());
            garbage.setPassportNo(registrationAdvance.getPassportNo());
            garbage.setDesignation(registrationAdvance.getDesignation());
            garbage.setAddressOffice(registrationAdvance.getAddressOffice());
            garbage.setAddressHome(registrationAdvance.getAddressHome());
            garbage.setPhoneOffice(registrationAdvance.getPhoneOffice());
            garbage.setPhoneHome(registrationAdvance.getPhoneHome());
            garbage.setHandPhone(registrationAdvance.getHandPhone());
            garbage.setEmail(registrationAdvance.getEmail());
            garbage.setEmergencyName(registrationAdvance.getEmergencyName());
            garbage.setEmergencyRelation(registrationAdvance.getEmergencyRelation());
            garbage.setEmergencyPhone(registrationAdvance.getEmergencyPhone());
            garbage.setKnowAboutProgram(registrationAdvance.getKnowAboutProgram());
            garbage.setApplicationSignature(registrationAdvance.getApplicationSignature());
            garbage.setDate(registrationAdvance.getDate());
            // garbage.setNonSingaporean(registrationAdvance.getNonSingaporean());
            garbage.setCompany(registrationAdvance.getCompany());
            // garbage.setPhoto(registrationAdvance.getPhoto());
            garbage.setApprove(registrationAdvance.getApprove());
            // garbage.setApplicationForm(registrationAdvance.getApplicationForm());
            // garbage.setWorkPermit(registrationAdvance.getWorkPermit());
            // garbage.setAcademicCertificates(registrationAdvance.getAcademicCertificates());
            // garbage.setAcademicTranscripts(registrationAdvance.getAcademicTranscripts());
            // garbage.setOthers(registrationAdvance.getOthers());
            // garbage.setSessionId(registrationAdvance.getSessionId());
            // garbage.setSessionIdDate(registrationAdvance.getSessionIdDate());
            garbage.setAmount(registrationAdvance.getAmount());
            garbage.setPaymentStatus(registrationAdvance.getPaymentStatus());
            garbage.setPaymentType(registrationAdvance.getPaymentType());
            garbage.setStartModulus(registrationAdvance.getStartModulus());
            garbage.setEndModulus(registrationAdvance.getEndModulus());
            garbage.setEnrolledDate(registrationAdvance.getEnrolledDate());
            // garbage.setGstAmount(registrationAdvance.getGstAmount());
            // garbage.setTotalAmount(registrationAdvance.getTotalAmount());
            // garbage.setEmiAmount(registrationAdvance.getEmiAmount());
            // garbage.setEmiMonths(registrationAdvance.getEmiMonths());
            // garbage.setPaymentDate(registrationAdvance.getPaymentDate());
            garbage.setStatus(registrationAdvance.getStatus());

            // Copy the collections or related entities if required
            garbage.setInstitutionDetails(new ArrayList<>(registrationAdvance.getInstitutionDetails()));
            garbage.setCourseFeeDetails(new ArrayList<>(registrationAdvance.getCourseFeeDetails()));
            garbage.setScheduleDate(new ArrayList<>(registrationAdvance.getScheduleDate()));
//            garbage.setModuleMarks(new ArrayList<>(registrationAdvance.getModuleMarks()));

            // Save the Garbage entity
            garbageRepository.save(garbage);

            // Delete the associated Student
            Long studentId = registrationAdvance.getStudent().getId();
            studentRepository.deleteById(studentId);

            // Delete the original RegistrationAdvance record after transferring
            registrationAdvanceRepository.delete(registrationAdvance);
        }
    }
    public List<Garbage> findAlumniRegistrationAdvancesByNric(String nric) {
        return garbageRepository. findBynric(nric);
    }
    public List<RegistrationAdvance> findRegistrationAdvanceByNric(String nric) {
        return registrationAdvanceRepository.findByNric(nric);
    }
    @Transactional
    @Scheduled(cron = "0 0 2 10 * ?")
    public void sendMonthlyEmail() throws IOException, MessagingException {
        List<String> studentEmails = registrationAdvanceRepository.getAllStudentEmails();
        String subject = "Monthly Update";
        Resource resource = new ClassPathResource("invoice.pdf");

        for (String userEmail : studentEmails) {
            try (InputStream inputStream = resource.getInputStream()) {
                byte[] pdfBytes = IOUtils.toByteArray(inputStream);

                List<RegistrationAdvance> registrationAdvances = registrationAdvanceRepository.findByEmail(userEmail);
                for (RegistrationAdvance registrationAdvance : registrationAdvances) {
                    if (registrationAdvance != null) {
                        RegistrationAdvanceInfo info = populateRegistrationAdvanceInfo(registrationAdvance);

                        // Generate custom invoice PDF for the student
                        ByteArrayOutputStream pdfOutputStream = modifyPdf1(new ByteArrayInputStream(pdfBytes), info);

                        // Create email body
                        String body = "This is your monthly update.\n\n" +
                                "Registration Fee Remaining: " + info.getRegistrationFeeRemaining() + "\n" +
                                "Course Fee Remaining: " + info.getCourseFeeRemaining() + "\n";

                        // Send email with custom invoice PDF attached
                        sendEmail(userEmail, subject, body, pdfOutputStream.toByteArray());
                        System.out.println("Email sent to " + userEmail + " at " + LocalDateTime.now());

                        // Close the ByteArrayOutputStream after use
                        pdfOutputStream.close();
                    }
                }
            } catch (IOException e) {
                // Handle IOException appropriately, e.g., log or rethrow
                e.printStackTrace();
            }
        }
    }

    private ByteArrayOutputStream modifyPdf1(InputStream pdfInvoice, RegistrationAdvanceInfo info) throws IOException {
        PDDocument document = PDDocument.load(pdfInvoice);
        PDPage page = document.getPage(0);

        // Load the Calibri font from resources
        InputStream fontStream = getClass().getResourceAsStream("/calibri.ttf");
        PDType0Font calibri = PDType0Font.load(document, fontStream);

        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);

        // Remove brackets and content inside them from the course name
        String courseName = info.getCourse().replaceAll("\\(.*?\\)", "").trim();

        // Convert the date format
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(info.getDate(), inputFormatter);
        String formattedDate = date.format(outputFormatter);

        contentStream.beginText();
        contentStream.setFont(calibri, 12);
        contentStream.setNonStrokingColor(0, 0, 0);
        contentStream.newLineAtOffset(118, 608);
        contentStream.showText(info.getName().toUpperCase());
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText(info.getNric().toUpperCase());
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText(courseName.toUpperCase());
        contentStream.newLineAtOffset(0, -16);
        contentStream.showText(info.getIntakeId().toUpperCase());
        contentStream.newLineAtOffset(350, 46);

        contentStream.newLineAtOffset(-78, -150);
        contentStream.showText(String.valueOf(info.getCourseFeeRemaining() ));
        contentStream.newLineAtOffset(0, -26);
        contentStream.showText(String.valueOf(info.getCourseFeeRemaining() ));
        contentStream.newLineAtOffset(0, -103);
        contentStream.showText(String.valueOf(info.getCourseFee() + info.getRegistrationFee()).toUpperCase() + ".00");
        contentStream.newLineAtOffset(-19, 320);
        contentStream.newLineAtOffset(-345, -422);
        contentStream.showText(info.getIntakeId().toUpperCase());
        contentStream.newLineAtOffset(0, -17);
        contentStream.showText(formattedDate.toUpperCase());
        contentStream.newLineAtOffset(319, 398);
        contentStream.showText("JA" + (info.getIntakeId()) + ("-" + String.valueOf(info.getId()).toUpperCase()));
        contentStream.newLineAtOffset(0, -16);

        contentStream.showText(formattedDate.toUpperCase());
        contentStream.newLineAtOffset(0, -15);

        contentStream.showText("CD");
        contentStream.endText();

        contentStream.close();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();

        return outputStream;
    }

    private void sendEmail(String to, String subject, String body, byte[] attachment) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // Ensure UTF-8 encoding
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, false); // Set the email body as plain text
        helper.addAttachment("invoice.pdf", new ByteArrayResource(attachment));
        mailSender.send(message);
    }

    public List<RegistrationAdvanceBatchDetailDTO> getActiveStudentsByBatchId(Long batchId) {
        List<RegistrationAdvance> registrations = registrationAdvanceRepository.findActiveByBatchId(batchId);

        return registrations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private RegistrationAdvanceBatchDetailDTO convertToDTO(RegistrationAdvance registration) {
        RegistrationAdvanceBatchDetailDTO dto = new RegistrationAdvanceBatchDetailDTO();
        dto.setBatch_id(registration.getBatch().getId());
        dto.setSName(registration.getStudent().getName());
        dto.setSEmail(registration.getStudent().getEmail());
        dto.setSPhone(registration.getStudent().getPhone());
//        dto.setStatus(Boolean.parseBoolean(registration.getApprove()));
        dto.setStatus(registration.getStudent().getStatus());
        System.out.println("vinoth"+registration.getStudent().getStatus());
        dto.setRegistered(registration.getEnrolledDate().toString()); // Adjust as per your date format
        dto.setSId(registration.getStudent().getId());


        return dto;
    }
    public Optional<Student> findStudentByNric(String nric) {
        // Implement logic to fetch student by NRIC
        return studentRepository.findByNric(nric);
    }

    public List<RegistrationAdvance> findByNricAndCheckAlumniStatus(String nric) {
        // Implement logic to fetch alumni registration advances by NRIC
        return registrationAdvanceRepository.findByNricAndStatus(nric, "Alumni");
    }

    public List<RegistrationAdvance> findByStudentId(Long studentId) {
        return registrationAdvanceRepository.findByStudentId(studentId);
    }
    public ByteArrayOutputStream sendEmailforApproval(String pdfUrl, RegistrationAdvance registrationAdvance) throws MessagingException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            // Retrieve student details
            Student student = registrationAdvance.getStudent();
            String studentName = student.getName();

            // Retrieve course name from batch
            String courseName = registrationAdvance.getBatch().getCourse();

            // Construct the email message with HTML content
            String emailBody = "<div style='background-color: #f5f5f5; padding: 20px;'>" +
                    "<div style='background-color: white; padding: 20px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);'>" +
                    "<h2 style='color: #333;'>Approval Confirmation from Jurong Academy</h2>" +
                    "<p style='color: #666;'>Dear " + studentName + ",</p>" +
                    "<p style='color: #666;'>We are pleased to inform you that your application has been successfully approved for the course: " + courseName + ".</p>" +
                    "<p style='color: #666;'>Thank you for choosing Jurong Academy.</p>" +pdfUrl+
                    "</div>" +
                    "</div>" +
                    "<br/>" +
                    "<img src='https://ja.edu.sg/wp-content/uploads/2022/10/logo-new01-1.png' alt='Jurong Academy Logo' style='display: block; margin: 0 auto;' height='80px'>" +
                    "<br/>" +
                    "<p style='color: #666;'>You can find the detailed approval document <a href='" + pdfUrl + "'>here</a>.</p>";

            // Create and send email with HTML content
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(registrationAdvance.getEmail());
            helper.setSubject("Your Registration Approval");
            helper.setText(emailBody, true);  // Set the second parameter to true to indicate HTML content

            mailSender.send(message);

            // Return outputStream for further use, if needed
            return outputStream;
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception appropriately
        } finally {
            outputStream.close();
        }

        return outputStream; // Handle appropriately if needed
    }
    public void sendWhatsAppMessageForApprove(String pdfUrl, RegistrationAdvance registrationAdvance) throws IOException, URISyntaxException, InterruptedException {
        String apiKey = "c4a984a808414ef8af95bf72658d8463";
        String url = "https://api.bulkwhatsapp.net/wapp/api/send";

        // Ensure that the provided PDF URL is valid
        if (pdfUrl == null || pdfUrl.isEmpty()) {
            throw new IllegalArgumentException("PDF URL must not be empty");
        }

        // Get student's phone number from RegistrationAdvance
        String phoneNumber = registrationAdvance.getHandPhone();
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("Phone number must not be empty");
        }

        // Construct the URI for WhatsApp message
        URI uri = new URI(url + "?apikey=" + apiKey +
                "&mobile=" + phoneNumber +
                "&msg=" + URLEncoder.encode(StandardCharsets.UTF_8.toString()) +
                "&pdf=" + URLEncoder.encode(pdfUrl, StandardCharsets.UTF_8.toString()));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to send WhatsApp message: " + response.body());
        }
    }
    @Transactional
    public List<StudentDetailsDTOs> getAllActiveStudentDetails() {
        List<RegistrationAdvance> registrationAdvances = registrationAdvanceRepository.findAll();

        List<StudentDetailsDTOs> activeStudentDetails = registrationAdvances.stream()
                .filter(registrationAdvance -> {
                    Student student = registrationAdvance.getStudent();
                    return student != null && "active".equalsIgnoreCase(student.getStatus());
                })
                .map(registrationAdvance -> {
                    // Fetching the Student entity associated with RegistrationAdvance
                    Student student = registrationAdvance.getStudent();

                    // Creating a new StudentDetailsDTOs object
                    StudentDetailsDTOs dto = new StudentDetailsDTOs();

                    // Setting values from the Student entity
                    dto.setName(student.getName()); // Student's name
                    dto.setEmail(student.getEmail()); // Student's email
                    dto.setPhone(student.getPhone()); // Student's phone number
                    dto.setProfilePicture(student.getProfileUrl()); // Student's profile picture URL

                    // Setting the registrationAdvanceId from the RegistrationAdvance entity
                    dto.setRegistrationAdvanceId(registrationAdvance.getId());

                    // Returning the populated DTO
                    return dto;
                })
                .collect(Collectors.toList());

        // Optional: Reverse the list if needed
        Collections.reverse(activeStudentDetails);

        return activeStudentDetails;
    }
    @Transactional
    public List<StudentDetailsDTO> getAllActiveStudentDetailss() {
        List<RegistrationAdvance> registrationAdvances = registrationAdvanceRepository.findAll();

        return registrationAdvances.stream()
                .filter(registrationAdvance -> {
                    Student student = registrationAdvance.getStudent();
                    return student != null && "active".equalsIgnoreCase(student.getStatus());
                })
                .map(registrationAdvance -> {
                    Student student = registrationAdvance.getStudent();

                    StudentDetailsDTO dto = new StudentDetailsDTO();

                    dto.setName(student.getName());
                    dto.setEmail(student.getEmail());
                    dto.setPhone(student.getPhone()); // Set the phone field
                    dto.setProfilePicture(student.getProfileUrl());
                    dto.setDob(student.getDob());
                    dto.setStatus(student.getStatus());
                    dto.setId(student.getId());
                    dto.setType(student.getType());
                    dto.setFullname(student.getFullname());
                    dto.setGender(student.getGender());
                    dto.setNric(student.getNric());
                    dto.setPassword(student.getPassword());
                    dto.setDesignation(student.getDesignation());
                    dto.setApprove(Boolean.parseBoolean(student.getApprove()));
                    dto.setRegistrationAdvanceId(registrationAdvance.getId());

                    return dto;
                })
                .collect(Collectors.toList());
    }
    public PaymentDetailsDTO getPaymentDetails(Long id) {
        RegistrationAdvance registrationAdvance = getRegistrationAdvanceById(id);
        if (registrationAdvance != null) {
            RegistrationAdvanceInfo info = populateRegistrationAdvanceInfo(registrationAdvance);

            PaymentDetailsDTO detailsDTO = new PaymentDetailsDTO();
            detailsDTO.setEmail(registrationAdvance.getEmail());
            detailsDTO.setPhoneNumber(registrationAdvance.getHandPhone().replace(" ", ""));
            detailsDTO.setStudentName(info.getName());
            detailsDTO.setNric(info.getNric());
            detailsDTO.setCourse(info.getCourse());
            detailsDTO.setIntakeId(registrationAdvance.getIntakeId());
            detailsDTO.setRegistrationFeeRemaining(info.getRegistrationFeeRemaining());
            detailsDTO.setCourseFeeRemaining(info.getCourseFeeRemaining());

            List<InstallmentDetailsDTO> installmentDetailsDTOs = new ArrayList<>();
            for (CourseFeeDetails installment : info.getInstallmentsPaid()) {
                InstallmentDetailsDTO installmentDTO = new InstallmentDetailsDTO();
                installmentDTO.setAmount(installment.getAmount());
                //installmentDTO.setInstallmentDescription(installment.getDescription()); // Assuming `description` field exists
                installmentDetailsDTOs.add(installmentDTO);
            }
            detailsDTO.setInstallments(installmentDetailsDTOs);


            return detailsDTO;
        }
        return null;
    }
    @Transactional
    public List<StudentDetailsDTO> getAllPendingStudentDetails() {
        List<RegistrationAdvance> registrationAdvances = registrationAdvanceRepository.findAll();

        return registrationAdvances.stream()
                .filter(registrationAdvance -> {
                    Student student = registrationAdvance.getStudent();
                    return student != null && "pending".equalsIgnoreCase(student.getStatus());
                })
                .map(registrationAdvance -> {
                    Student student = registrationAdvance.getStudent();

                    StudentDetailsDTO dto = new StudentDetailsDTO();

                    dto.setName(student.getName());
                    dto.setEmail(student.getEmail());
                    dto.setPhone(student.getPhone()); // Set the phone field
                    dto.setProfilePicture(student.getProfileUrl());
                    dto.setDob(student.getDob());
                    dto.setStatus(student.getStatus());
                    dto.setId(student.getId());
                    dto.setType(student.getType());
                    dto.setFullname(student.getFullname());
                    dto.setGender(student.getGender());
                    dto.setNric(student.getNric());
                    dto.setPassword(student.getPassword());
                    dto.setDesignation(student.getDesignation());
                    dto.setApprove(Boolean.parseBoolean(student.getApprove()));
                    dto.setRegistrationAdvanceId(registrationAdvance.getId());

                    return dto;
                })
                .collect(Collectors.toList());
    }
    public List<RegistrationAdvance> getAllByYear(int userYear) {
        List<RegistrationAdvance> allRecords = registrationAdvanceRepository.findAll();
        List<RegistrationAdvance> filteredRecords = new ArrayList<>();
        for (RegistrationAdvance registrationAdvance : allRecords) {
            List<ScheduleDate> scheduleDates = registrationAdvance.getScheduleDate();
            if (scheduleDates == null || scheduleDates.isEmpty()) {
                continue;
            }
            // Extract startDate from the first element
            LocalDate startDate = scheduleDates.get(0).getStartDate();
            // Extract endDate from the last element
            LocalDate endDate = scheduleDates.get(scheduleDates.size() - 1).getEndDate();

            YearMonth startYearMonth = YearMonth.from(startDate);
            YearMonth endYearMonth = YearMonth.from(endDate);

            int startYear = startYearMonth.getYear();
            int endYear = endYearMonth.getYear();

            // Check if userYear is within the startYear and endYear range
            if (userYear >= startYear && userYear <= endYear) {
                filteredRecords.add(registrationAdvance);
            }
        }
        return filteredRecords;
    }
    public List<RegistrationAdvance> getActiveRegistrationsByIntakeId(String intakeId) {
        return registrationAdvanceRepository.findByIntakeIdAndStatus(intakeId, "active");
    }

    public List<RegistrationAdvance> getActiveRegistrationsByIntakeIdalumni(String intakeId) {
        return registrationAdvanceRepository.findByIntakeIdAndStatus(intakeId, "alumni");
    }

    public List<StudentInfoDTO> getStudentsByIntakeId(String intakeId) {
        return registrationAdvanceRepository.findStudentsByIntakeId(intakeId);
    }

    public RegistrationAdvanceResponseDTO updateModuleMarks(Long registrationAdvanceId, List<ModuleMark> moduleMarks) {
        RegistrationAdvance registrationAdvance = registrationAdvanceRepository.findById(registrationAdvanceId).orElse(null);
        if (registrationAdvance == null) {
            return null; // Handle case where RegistrationAdvance is not found
        }

        Batch batch = registrationAdvance.getBatch();
        int requiredMarks = batch.isAssignment() ? 2 : 1;

        for (ModuleMark moduleMark : moduleMarks) {
            if (requiredMarks == 2) {
                if (moduleMark.getMark1() == null || moduleMark.getMark2() == null) {
                    throw new IllegalArgumentException("Each module must have exactly 2 marks for assignments (mark1 and mark2).");
                }
            } else {
                if (moduleMark.getMark1() == null) {
                    throw new IllegalArgumentException("Each module must have at least 1 mark (mark1).");
                }
            }
        }

        moduleMarks.forEach(moduleMark -> moduleMark.setRegistrationAdvance(registrationAdvance));
        moduleMarkRepository.saveAll(moduleMarks);
        registrationAdvanceRepository.save(registrationAdvance);

        // Create DTO response
        RegistrationAdvanceResponseDTO responseDTO = new RegistrationAdvanceResponseDTO();
        responseDTO.setId(registrationAdvance.getId());
        responseDTO.setName(registrationAdvance.getStudent().getName());
        responseDTO.setEmail(registrationAdvance.getStudent().getEmail());
        responseDTO.setCourse(registrationAdvance.getBatch().getCourse());

        // Convert ModuleMarks to ModuleMarkDTOs
        List<ModuleMarkDTO> moduleMarkDTOs = moduleMarks.stream().map(moduleMark -> {
            ModuleMarkDTO dto = new ModuleMarkDTO();
            dto.setId(moduleMark.getId());
            dto.setMark1(moduleMark.getMark1());
            dto.setMark2(moduleMark.getMark2());
            dto.setTotal(moduleMark.getTotal());
            return dto;
        }).collect(Collectors.toList());

        responseDTO.setModuleMarks(moduleMarkDTOs);

        return responseDTO;
    }

    public RegistrationAdvanceResponseDTO updateModuleMarks1(Long registrationAdvanceId, List<ModuleMark> moduleMarks) {
        // Find the existing RegistrationAdvance entity
        RegistrationAdvance registrationAdvance = registrationAdvanceRepository.findById(registrationAdvanceId).orElse(null);
        if (registrationAdvance == null) {
            return null; // Handle case where RegistrationAdvance is not found
        }

        Batch batch = registrationAdvance.getBatch();
        int requiredMarks = batch.isAssignment() ? 2 : 1;

        // Validate the number of marks per module
        for (ModuleMark moduleMark : moduleMarks) {
            if ((batch.isAssignment() && (moduleMark.getMark1() == null || moduleMark.getMark2() == null)) ||
                    (!batch.isAssignment() && moduleMark.getMark1() == null)) {
                throw new IllegalArgumentException("Each module must have exactly " + requiredMarks + " marks.");
            }
        }

        // Set registrationAdvance reference for each module mark
        moduleMarks.forEach(moduleMark -> moduleMark.setRegistrationAdvance(registrationAdvance));

        // Process existing module marks
        List<ModuleMark> existingModuleMarks = moduleMarkRepository.findByRegistrationAdvanceId(registrationAdvanceId);

        for (ModuleMark incomingModuleMark : moduleMarks) {
            boolean isUpdated = false;
            for (ModuleMark existingModuleMark : existingModuleMarks) {
                if (incomingModuleMark.getModuleName().equals(existingModuleMark.getModuleName())) {
                    existingModuleMark.setMark1(incomingModuleMark.getMark1());
                    existingModuleMark.setMark2(incomingModuleMark.getMark2());
                    existingModuleMark.setTotal(incomingModuleMark.getTotal());
                    isUpdated = true;
                    break;
                }
            }
            if (!isUpdated) {
                // If the module mark does not exist, add it as a new entry
                moduleMarkRepository.save(incomingModuleMark);
            }
        }

        // Save updated module marks
        moduleMarkRepository.saveAll(existingModuleMarks);
        // Save updated registrationAdvance
        registrationAdvanceRepository.save(registrationAdvance);

        // Prepare response DTO
        RegistrationAdvanceResponseDTO responseDTO = new RegistrationAdvanceResponseDTO();
        responseDTO.setId(registrationAdvance.getId());
        responseDTO.setName(registrationAdvance.getStudent().getName());
        responseDTO.setEmail(registrationAdvance.getStudent().getEmail());
        responseDTO.setCourse(registrationAdvance.getBatch().getCourse());

        List<ModuleMarkDTO> moduleMarkDTOs = moduleMarks.stream().map(moduleMark -> {
            ModuleMarkDTO dto = new ModuleMarkDTO();
            dto.setId(moduleMark.getId());
            dto.setMark1(moduleMark.getMark1());
            dto.setMark2(moduleMark.getMark2());
            dto.setTotal(moduleMark.getTotal());
            return dto;
        }).collect(Collectors.toList());

        responseDTO.setModuleMarks(moduleMarkDTOs);

        return responseDTO;
    }
    public List<RegistrationAdvance> getActiveRegistrationsByCategoryId(Long categoryId) {
        // Find all batches by category ID
        List<Batch> batches = batchRepository.findByCategoryId(categoryId);

        // Collect all active students (registrations) for each batch
        List<RegistrationAdvance> activeRegistrations = new ArrayList<>();

        for (Batch batch : batches) {
            // Fetch all active students (status = 'active') for each batch
            List<RegistrationAdvance> activeBatchRegistrations = registrationAdvanceRepository.findByBatchIdAndStatus(batch.getId(), "active");
            activeRegistrations.addAll(activeBatchRegistrations);
        }

        return activeRegistrations;
    }

    public List<StudentDTO> getStudentsByBatchAndIntake(String intakeId) {
        return registrationAdvanceRepository.findStudentsByBatchIdAndIntake(intakeId);
    }

    public List<RegistrationAdvance> getRegistrationsByStatus() {
        return registrationAdvanceRepository.findByStatusNewOrRegister();
    }

    public List<RegistrationAdvance> getStudentsByEnrollmentYearAndStatus(int startYear, int endYear) {
        return registrationAdvanceRepository.findByEnrollmentYearBetweenAndStatusAndNotInGarbage(startYear, endYear);
    }
    public long countNewRegistrations() {
        return registrationAdvanceRepository.countByStatusNew();
    }
    public long countActiveRegistrationsByCategoryId(Long categoryId) {
        // Find all batches by category ID
        List<Batch> batches = batchRepository.findByCategoryId(categoryId);

        // Sum up the count of active students for all batches
        long totalActiveStudents = 0;

        for (Batch batch : batches) {
            long activeCount = registrationAdvanceRepository.countActiveByBatchId(batch.getId());
            System.out.println("Batch ID: " + batch.getId() + ", Active Count: " + activeCount); // Debugging line

            // For additional debugging, get all active registrations in the batch
            List<RegistrationAdvance> activeRegistrations = registrationAdvanceRepository.findAllActiveByBatchId(batch.getId());
            System.out.println("Batch ID: " + batch.getId() + ", Active Registrations: " + activeRegistrations.size()); // Debugging line

            totalActiveStudents += activeCount;
        }

        return totalActiveStudents;
    }

}
