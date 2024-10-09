package org.kreyzon.stripe.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CourseFeeDetailsDTO {
    private String sessionId;
    private Long amount;
    private String paymentMode;
    private String remarks;
    private Long courseFeeRemaining;
    private Long registrationFeeRemaining;
}
