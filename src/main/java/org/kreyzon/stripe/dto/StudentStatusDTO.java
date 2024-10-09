package org.kreyzon.stripe.dto;

public class StudentStatusDTO {
    private String status;

    public StudentStatusDTO(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
