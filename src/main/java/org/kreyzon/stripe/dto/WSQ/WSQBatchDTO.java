package org.kreyzon.stripe.dto.WSQ;

import org.kreyzon.stripe.entity.wsq.WSQBatchDay;
import org.kreyzon.stripe.entity.wsq.WSQCourse;
import org.kreyzon.stripe.entity.wsq.WSQFacilitator;
import org.kreyzon.stripe.entity.wsq.WSQVenue;

import java.util.List;

public class WSQBatchDTO {
    private Long id;
    private String name;
    private int intakeSize;
    private WSQCourse course;
    private List<WSQFacilitator> facilitators;
    private List<WSQFacilitator> assessor;
    private WSQVenue venue;
    private List<WSQBatchDay> days;
}
