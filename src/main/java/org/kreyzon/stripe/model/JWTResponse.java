package org.kreyzon.stripe.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class JWTResponse {
    private String jwtToken;
    private String userId;
    private String username;
    private String name;
}
