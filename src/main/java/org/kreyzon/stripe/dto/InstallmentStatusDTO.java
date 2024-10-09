package org.kreyzon.stripe.dto;

import java.util.List;

public class InstallmentStatusDTO {
    private String message;
    private List<InstallmentDetail> installmentDetails;

    // Constructor for error message
    public InstallmentStatusDTO(String message) {
        this.message = message;
    }

    // Default constructor
    public InstallmentStatusDTO() {}

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<InstallmentDetail> getInstallmentDetails() {
        return installmentDetails;
    }

    public void setInstallmentDetails(List<InstallmentDetail> installmentDetails) {
        this.installmentDetails = installmentDetails;
    }
}
