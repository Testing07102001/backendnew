package org.kreyzon.stripe.dto;

import java.time.LocalDate;

public class StudentDetailsDTO {
    private String name;
    private String email;
    private String phone;  // Ensure this field exists
    private String fullname;
    private String gender;
    private String designation;
    private String status;
    private String type;
    private boolean approve;
    private LocalDate dob;
    private String password;
    private String ProfilePicture;
    private Long id;
    // getters and setters...
    private Long RegistrationAdvanceId;
 private String Nric;

    public String getNric() {
        return Nric;
    }

    public void setNric(String nric) {
        Nric = nric;
    }

    public Long getRegistrationAdvanceId() {
        return RegistrationAdvanceId;
    }

    public void setRegistrationAdvanceId(Long registrationAdvanceId) {
        RegistrationAdvanceId = registrationAdvanceId;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isApprove() {
        return approve;
    }

    public void setApprove(boolean approve) {
        this.approve = approve;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePicture() {
        return ProfilePicture;
    }

    public void setProfilePicture(String ProfilePicture) {
        this.ProfilePicture = ProfilePicture;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
