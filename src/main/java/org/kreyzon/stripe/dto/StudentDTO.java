package org.kreyzon.stripe.dto;

public class StudentDTO {
    private Long studentId;  // Added studentId
    private String name;
    private String nric;

    public StudentDTO(Long studentId, String name, String nric) {
        this.studentId = studentId;
        this.name = name;
        this.nric = nric;
    }

    // Getters and setters
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }
}
