package org.kreyzon.stripe.dto;

import java.util.List;

public class PaymentDetailsDTO {
    private String email;
    private String phoneNumber;
    private List<InstallmentDetailsDTO> installments;
    private String studentName;
    private String nric;
    private String course;
    private String intakeId;
    private Long registrationFeeRemaining;
    private Long courseFeeRemaining;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<InstallmentDetailsDTO> getInstallments() {
        return installments;
    }

    public void setInstallments(List<InstallmentDetailsDTO> installments) {
        this.installments = installments;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getIntakeId() {
        return intakeId;
    }

    public void setIntakeId(String intakeId) {
        this.intakeId = intakeId;
    }

    public Long getRegistrationFeeRemaining() {
        return registrationFeeRemaining;
    }

    public void setRegistrationFeeRemaining(Long registrationFeeRemaining) {
        this.registrationFeeRemaining = registrationFeeRemaining;
    }

    public Long getCourseFeeRemaining() {
        return courseFeeRemaining;
    }

    public void setCourseFeeRemaining(Long courseFeeRemaining) {
        this.courseFeeRemaining = courseFeeRemaining;
    }
}
