package org.kreyzon.stripe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class StudentScheduleDTO {
    private Long id;
    private StudentInfoDTO studentInfo;
    private List<ScheduleDateDTO> scheduleDates;
}
