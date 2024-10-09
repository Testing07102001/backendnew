package org.kreyzon.stripe.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.time.LocalDate;


@Getter
@Setter
@Embeddable
public class ScheduleDate {
    private int startModulus;
    private LocalDate startDate;
    private LocalDate endDate;
}
