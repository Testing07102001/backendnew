package org.kreyzon.stripe.dto;

import lombok.Getter;
import lombok.Setter;
import org.kreyzon.stripe.entity.InstitutionDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class EnrollmentDTO {
 private Long Id;
 private String studentName;
 private String studentEmail;
 private String name;
 private Long batchId;
 private int startModulus;
 private LocalDate enrollmentDate;
 private String nric;
 private String phone;
 private String email;
 private String gender;
 private LocalDate dob;
 private String password;
 private String address;
 private String postalcode;
 private String nationality;
 private String race;
 private String eduction;
 private String employmentstatus;
 private String salary;
 private String companyname;
 private String corporatecompanysponsored;
 private String designation;
 private String type;
 private String title;
 private String intake;
 private String sureName;
 private String countryBirth;
 private String NRICno;
 private String passportNo;
 private String addressOffice;
 private String addressHome;
 private String phoneOffice;
 private String phoneHome;
 private String handPhone;
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
 private String sessionId;
 private String sessionIdDate;
 private Long amount;
 private String paymentStatus;
 private String status;
 // private String applicationForm;
// private String workPermit;
// private String academicCertificates;
// private String academicTranscripts;
// private String others;
 private String workPermitSpass;
 private String repass;
 private String dependentPass;
 private String passport;
 private Map<String, String> markSheet;
 private Map<String, String> certificate;
 private String transcript;
 private String profilePicture;
 private List<InstitutionDetails> institutionDetails;
 private List<ModulusDTO> moduli;
 private String paymentType;
 private String companyName;
 private String companyContact;
 private String companyAddress;
 private Long registrationFee;
 private String proofType;
 private LocalDateTime approveDate;
 private String emergencyOtherDetails;
 private LocalDateTime paidDate;
 @Getter
 @Setter
 private String intakeId;
 private String approveReference;
 private String otherDetails;
 private String referalfriend;
 private boolean isLate;
    // getters and setters
}
