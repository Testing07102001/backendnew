package org.kreyzon.stripe.dto;

import java.time.LocalDate;

public class AlumniDetailsDTO {
    private String nric;
    private String name;
    private String email;
    private Long id;
    private String status;
    private LocalDate dob;
    private String intakeId;
    private String fullname;
    private String gender;

    // Constructors, getters, and setters
    public AlumniDetailsDTO() {}

    public AlumniDetailsDTO(String nric, String name, String email, Long id, String status, LocalDate dob, String intakeId, String fullname, String gender) {
        this.nric = nric;
        this.name = name;
        this.email = email;
        this.id = id;
        this.status = status;
        this.dob = dob;
        this.intakeId = intakeId;
        this.fullname = fullname;
        this.gender = gender;
    }

    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getIntakeId() {
        return intakeId;
    }

    public void setIntakeId(String intakeId) {
        this.intakeId = intakeId;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

}
