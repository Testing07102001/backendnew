package org.kreyzon.stripe.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
public class RegistrationAdvance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private String title;
    private String intake;
    private String name;
    private String countryBirth;
    private String dob;
    private String gender;
    private String nationality;
    private String nric;
    private String proofType;
    private String passportNo;
    private String designation;
    private String addressOffice;
    private String addressHome;
    private String phoneOffice;
    private String phoneHome;
    private String handPhone;
    private String email;
    private String emergencyName;
    private String emergencyRelation;
    private String emergencyPhone;
    private String knowAboutProgram;
    private String applicationSignature;
    private String date;
    private String company;
    private String approver;
    private Long discountAmount;
    private String companyName;
    private String companyContact;
    private String companyAddress;
    private Long registrationFee;
    private String approve;
    private String workPermitSpass;
    private String repass;
    private String dependentPass;
    private String passport;
    private String paymentType;
    private boolean emailCourseSent;
    private Long courseFee;
    private String emergencyOtherDetails;
    private String approverId;
    @ElementCollection
    @CollectionTable(name = "registration_advance_mark_sheet", joinColumns = @JoinColumn(name = "registration_advance_id"))
    @MapKeyColumn(name = "level")
    @Column(name = "url")
    private Map<String, String> markSheet;
    @ElementCollection
    @CollectionTable(name = "registration_advance_certificate", joinColumns = @JoinColumn(name = "registration_advance_id"))
    @MapKeyColumn(name = "level")
    @Column(name = "url")
    private Map<String, String> certificate;

    private String transcript;
    private String profilePicture;
    private Long amount;
    private String paymentStatus;
    @Column(name = "sequence_number")
    private Integer sequenceNumber; // Field to store the sequence number

    @Column(name = "email_sent")
    private boolean emailSent;
    @ElementCollection
    private List<InstitutionDetails> institutionDetails;
    @ElementCollection
    private List<CourseFeeDetails> courseFeeDetails = new ArrayList<>();
    @ElementCollection
    private List<ScheduleDate> scheduleDate;
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;
    @OneToMany(mappedBy = "registrationAdvance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ModuleMark> moduleMarks;

    private int startModulus;
    private int endModulus;
    private LocalDate enrolledDate;

    private String status;
    private double RegistrationFeePaid;
    private double CourseFeePaid;
    //    private String intakeId;
    private boolean finalPaymentConfirmed;
    private boolean emailRegistrationSent;
    @Column(name = "intake_id")
    private String intakeId;

    private LocalDate approveDate;
    private boolean isLate;

    private String approveReference;
    private LocalDateTime paidDate; // New field to store the date when status is set to "paid"

    private String otherDetails;
    private String referalfriend;
    private String studentIntakeId;
public Integer getFormattedSequenceNumber() {
    if (sequenceNumber == null) {
        sequenceNumber = 1; // Initialize if null
    } else {
        sequenceNumber += 1; // Increment sequence number
        // Reset sequence number to 1 if it exceeds 9999
        if (sequenceNumber > 9999) {
            sequenceNumber = 1;
        }
    }
    // Format as 0001, 0002, etc., and then convert to Integer
    String formatted = String.format("%04d", sequenceNumber);
    return Integer.valueOf(formatted); // Convert formatted string to Integer
}

}
