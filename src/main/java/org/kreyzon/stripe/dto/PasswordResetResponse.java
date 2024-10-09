package org.kreyzon.stripe.dto;

public class PasswordResetResponse {
    private String message;

    // Constructor to set the message
    public PasswordResetResponse(String message) {
        this.message = message;
    }

    // Getter and setter for the 'message' field
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
