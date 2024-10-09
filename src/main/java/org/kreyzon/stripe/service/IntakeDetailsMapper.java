package org.kreyzon.stripe.service;

import org.kreyzon.stripe.dto.IntakeDetailsDTO;
import org.kreyzon.stripe.entity.IntakeDetails;
import org.kreyzon.stripe.entity.ScheduleDateDetails;

import java.util.List;
import java.util.stream.Collectors;

public class IntakeDetailsMapper {

    public static IntakeDetailsDTO toDto(IntakeDetails intakeDetails) {
        IntakeDetailsDTO dto = new IntakeDetailsDTO();
        dto.setIntakeId(intakeDetails.getIntakeId());
        dto.setBatchId(intakeDetails.getBatchId());
        dto.setId(intakeDetails.getId());

        List<ScheduleDateDetails> scheduleDates = intakeDetails.getScheduleDateDetails();
        if (scheduleDates != null && !scheduleDates.isEmpty()) {
            dto.setStartDate(scheduleDates.get(0).getStartDate());
            dto.setEndDate(scheduleDates.get(scheduleDates.size() - 1).getEndDate());
        }



        return dto;
    }


}
