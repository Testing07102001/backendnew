package org.kreyzon.stripe.dto;

import lombok.Data;

@Data
public class CreatePaymentRequest {
    private Long amount;
    private Long quantity;
    private String currency;
    private String name;
    private String successUrl;
    private String cancelUrl;
    private String client_reference_id;
    private String email;
}
