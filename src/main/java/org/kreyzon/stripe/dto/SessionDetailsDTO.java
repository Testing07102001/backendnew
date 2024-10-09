package org.kreyzon.stripe.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SessionDetailsDTO {
    private String sessionId;
    private Long amount;
    private String sessionIdDate;
    private String paymentType;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getSessionIdDate() {
        return sessionIdDate;
    }

    public void setSessionIdDate(String sessionIdDate) {
        this.sessionIdDate = sessionIdDate;
    }
}
