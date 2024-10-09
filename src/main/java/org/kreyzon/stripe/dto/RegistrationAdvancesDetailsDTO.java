package org.kreyzon.stripe.dto;

public class RegistrationAdvancesDetailsDTO {
    private String studentName;
    private Long registrationId;
    private String email;
    private String phone;
    private String gender;
    private String department;
    private String venue;
    private String nric;
    private String courseFee;
    private String overallStartDate;
    private String overallEndDate;
    private String address;
    private String nationality;
    private String status;
    private String dob;
    private double blanceAmount;
    private double registrationFeeRemaining;
    private String intakeId;

    public String getIntakeId() {
        return intakeId;
    }

    public void setIntakeId(String intakeId) {
        this.intakeId = intakeId;
    }

    public double getRegistrationFeeRemaining() {
        return registrationFeeRemaining;
    }

    public void setRegistrationFeeRemaining(double registrationFeeRemaining) {
        this.registrationFeeRemaining = registrationFeeRemaining;
    }

    public double getCourseFeeRemaining() {
        return courseFeeRemaining;
    }

    public void setCourseFeeRemaining(double courseFeeRemaining) {
        this.courseFeeRemaining = courseFeeRemaining;
    }

    private double courseFeeRemaining;
    public double getBlanceAmount() {
        return blanceAmount;
    }

    public void setBlanceAmount(double blanceAmount) {
        this.blanceAmount = blanceAmount;
    }
    // Getters and setters
    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public String getCourseFee() {
        return courseFee;
    }

    public void setCourseFee(String courseFee) {
        this.courseFee = courseFee;
    }





    public String getOverallStartDate() {
        return overallStartDate;
    }

    public void setOverallStartDate(String overallStartDate) {
        this.overallStartDate = overallStartDate;
    }

    public String getOverallEndDate() {
        return overallEndDate;
    }

    public void setOverallEndDate(String overallEndDate) {
        this.overallEndDate = overallEndDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }

}
