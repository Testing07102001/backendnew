package org.kreyzon.stripe.dto.WSQ;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionRequestDTO {
    private String session;
    private String startTime;
    private String endTime;
}
