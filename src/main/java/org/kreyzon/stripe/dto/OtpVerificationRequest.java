package org.kreyzon.stripe.dto;

public class OtpVerificationRequest {
    private String email;
    private String enteredOtp;

    // Constructors
    public OtpVerificationRequest() {
    }

    public OtpVerificationRequest(String email, String enteredOtp) {
        this.email = email;
        this.enteredOtp = enteredOtp;
    }

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEnteredOtp() {
        return enteredOtp;
    }

    public void setEnteredOtp(String enteredOtp) {
        this.enteredOtp = enteredOtp;
    }

}
