package org.kreyzon.stripe.dto.WSQ;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WSQBatchRequestDTO {
    private String name;
    private int intakeSize;
    private Long courseId;
    private Long venueId;
    private List<DayRequestDTO> days;
}