package org.kreyzon.stripe.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class Garbage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long registrationAdvanceId;

    private Long studentId;
    private String type;
    private String title;
    private String intakeId;
    private String name;
    private String sureName;
    private String countryBirth;
    private String dob;
    private String gender;
    private String nationality;
    private String nric;
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
    private String nonSingaporean;
    private String company;
    private String photo;
    private String approve;
    private String applicationForm;
    private String workPermit;
    private String academicCertificates;
    private String academicTranscripts;
    private String others;
    private String sessionId;
    private String sessionIdDate;
    private Long amount;
    private String paymentStatus;
    private String paymentType;

    // Element collections for other fields
    @ElementCollection
    private List<InstitutionDetails> institutionDetails;

    @ElementCollection
    private List<CourseFeeDetails> courseFeeDetails;

    @OneToMany
    private List<ModuleMark> moduleMarks;

    @ElementCollection
    private List<ScheduleDate> scheduleDate;

    @ManyToOne
    @JoinColumn(name = "batch_id", nullable = true)
    private Batch batch;

    private int startModulus;
    private int endModulus;
    private LocalDate enrolledDate;
    private double gstAmount;
    private double totalAmount;
    private double emiAmount;
    private int emiMonths;
    private LocalDate paymentDate;
    private String status;

    // Other fields and relationships
}
