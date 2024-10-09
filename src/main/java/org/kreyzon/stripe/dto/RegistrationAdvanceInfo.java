package org.kreyzon.stripe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kreyzon.stripe.entity.CourseFeeDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationAdvanceInfo {
    private Long id;
    private String name;
    private String sureName;
    private String addressHome;
    private String date;
    private String email;
    private Long registrationFee;
    private Long courseFee;
    private List<Integer> installmentCounts; // Add this field
    private Long studentId;
    private List<CourseFeeDetails> courseFeeDetails;
    private List<CourseFeeDetails> installmentsPaid;
   private  Long registrationFeeRemaining;
   private String passportNo;
   private String course;
   private String  intakeId;
   private String nric;
   private String approver;
    private boolean emailSent;
    private Long CourseFeeRemaining;
      private String paymentType;
    private int startModulus;
    private LocalDate scheduleDate;
    private int sequenceNumber;
    private boolean isLate;
    private LocalDateTime approveDate;
    private LocalDate startModuleDate;
}
