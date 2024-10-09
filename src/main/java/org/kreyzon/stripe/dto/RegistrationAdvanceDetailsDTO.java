package org.kreyzon.stripe.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RegistrationAdvanceDetailsDTO {
    private List<ScheduleDateDTO> scheduleDate;
    private String course;
    private String venue;
    private String studentName;
    private String nric;
    private String type;
    private String date;


    public RegistrationAdvanceDetailsDTO(String course, String venue, String studentName, String nric, String type, String date) {
        this.course = course;
        this.venue = venue;
        this.studentName = studentName;
        this.nric = nric;
        this.type = type;
        this.date = date;
    }

}
