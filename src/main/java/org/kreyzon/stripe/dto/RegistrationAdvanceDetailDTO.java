package org.kreyzon.stripe.dto;

public class RegistrationAdvanceDetailDTO {
    private String courseName;
    private String studentName;
    private Long registrationFee;
    private Long courseFee;
    private Long totalFee;

    public RegistrationAdvanceDetailDTO(String courseName, String studentName, Long registrationFee, Long courseFee, Long totalFee) {
        this.courseName = courseName;
        this.studentName = studentName;
        this.registrationFee = registrationFee;
        this.courseFee = courseFee;
        this.totalFee = totalFee;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Long getRegistrationFee() {
        return registrationFee;
    }

    public void setRegistrationFee(Long registrationFee) {
        this.registrationFee = registrationFee;
    }

    public Long getCourseFee() {
        return courseFee;
    }

    public void setCourseFee(Long courseFee) {
        this.courseFee = courseFee;
    }

    public Long getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Long totalFee) {
        this.totalFee = totalFee;
    }
}
