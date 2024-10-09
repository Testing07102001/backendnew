package org.kreyzon.stripe.dto.WSQ;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class DayRequestDTO {
    private LocalDate date; // Date for the batch day
    private List<SessionRequestDTO> sessions; // List of sessions for the day
}
