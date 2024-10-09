package org.kreyzon.stripe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionDto {
    private String sessionId;
    private String paid;
    private Long amount;
    private String name;
    private String client_reference_id;
}
