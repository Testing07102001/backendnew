package org.kreyzon.stripe.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@Embeddable
public class InstitutionDetails {
    private String institution;
    private String year;
    private String toYear;
    private String fullTimeOrPartTime;
    private String qualificationsAchieved;
}
