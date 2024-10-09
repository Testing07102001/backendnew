package org.kreyzon.stripe.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class StripeResponse<T> {
    private String status;
    private String message;
    private Integer httpStatus;
    private T data;

    public StripeResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public StripeResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public StripeResponse(String status, String message, Integer httpStatus) {
        this.status = status;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public StripeResponse(String status, String message, Integer httpStatus, T data) {
        this.status = status;
        this.message = message;
        this.httpStatus = httpStatus;
        this.data = data;
    }
}
