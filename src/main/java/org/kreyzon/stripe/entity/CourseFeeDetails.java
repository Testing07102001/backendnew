package org.kreyzon.stripe.entity;

import lombok.Getter;
import lombok.Setter;
import org.kreyzon.stripe.dto.InstallmentDetail;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@Getter
@Setter
public class CourseFeeDetails {
    private String sessionId;
    private LocalDate sessionDate;
    private Long amount;
    private String paymentMode;
    private String remarks;
    private String status;
    private Integer installmentCount;
    private Long courseFeeRemaining;
    private Long registrationFeeRemaining;
    private boolean emailSent;
    private int sequenceNumber;
    private boolean emailCourseSent;
    private String type;
    private Long remainingBalance; // Add this field

    private ArrayList<Integer> installmentCounts= new ArrayList<>();

//   Use List instead of ArrayList

    // Constructor
    public CourseFeeDetails() {
        // Ensure that the list is always initialized
//        this.installmentCounts = new ArrayList<>();
    }

    // Method to add an installment count
    public void addInstallmentCount(int count) {
//        if (this.installmentCounts == null) {
//            this.installmentCounts = new ArrayList<>(); // Ensure the list is initialized before using it
//        }
        this.installmentCounts.add(count);
    }
//    // Getter and setter for emailSent
//    public boolean isEmailSent() {
//        return emailSent;
//    }
//
//    public void setEmailSent(boolean emailSent) {
//        this.emailSent = emailSent;
//    }
public List<InstallmentDetail> calculateInstallmentDetails(LocalDate currentDate) {
    List<InstallmentDetail> installmentDetails = new ArrayList<>();
    // Calculate the number of months passed since the session date
    long monthsPassed = sessionDate != null ? java.time.temporal.ChronoUnit.MONTHS.between(sessionDate.withDayOfMonth(1), currentDate.withDayOfMonth(1)) : 0;

    for (int i = 1; i <= monthsPassed; i++) {
        // Calculate total due amount for each installment due
        Long totalDueAmount = remainingBalance + (i * (courseFeeRemaining / installmentCount));
        installmentDetails.add(new InstallmentDetail(i, totalDueAmount));
    }

    return installmentDetails;
}
}
