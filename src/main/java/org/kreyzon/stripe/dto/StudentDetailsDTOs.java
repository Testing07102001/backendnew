package org.kreyzon.stripe.dto;

import java.time.LocalDate;

public class StudentDetailsDTOs {


    private String name;
    private String email;
    private String phone;

    public Long getRegistrationAdvanceId() {
        return registrationAdvanceId;
    }

    public void setRegistrationAdvanceId(Long registrationAdvanceId) {
        this.registrationAdvanceId = registrationAdvanceId;
    }






    private String profilePicture;
    private Long registrationAdvanceId; // Add this field


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

    public void setPhone(String PhoneNumber) {
        this.phone = phone;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
