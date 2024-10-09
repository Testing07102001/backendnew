package org.kreyzon.stripe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentResponse {
    private String sessionId;
    private String sessionUrl;
    private String name;
    private String email;

    private String client_reference_id;
}
