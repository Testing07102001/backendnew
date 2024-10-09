package org.kreyzon.stripe.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class IntakeDetailsDTO {

    private String intakeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long batchId;
    private Long Id;
//    private List<ScheduleDateDetailsDTO> scheduleDateDetails;

    // Inner DTO class for ScheduleDateDetails
//    @Getter
//    @Setter
//    public static class ScheduleDateDetailsDTO {
//        private Long id;
//        private LocalDate startDate;
//        private LocalDate endDate;
//        private String venue;
//        private String trainer;
//        // add other necessary fields from ScheduleDateDetails
//    }
}
