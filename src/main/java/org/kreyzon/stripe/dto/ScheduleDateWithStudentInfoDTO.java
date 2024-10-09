package org.kreyzon.stripe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ScheduleDateWithStudentInfoDTO {
    private Long id;
    private int startModulus;
    private LocalDate startDate;
    private LocalDate endDate;
    private String studentName;
    private String email;
    private String studentPhoneNumber;

}
