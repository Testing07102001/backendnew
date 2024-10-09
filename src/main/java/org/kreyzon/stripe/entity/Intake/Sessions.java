package org.kreyzon.stripe.entity.Intake;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Getter
@Setter
@Embeddable
public class Sessions {
    private String startTime;
    private String endTime;
}