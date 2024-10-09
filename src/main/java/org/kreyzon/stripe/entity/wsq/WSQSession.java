package org.kreyzon.stripe.entity.wsq;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Getter
@Setter
@Embeddable
public class WSQSession {
    private String session;
    private String startTime;
    private String endTime;
}
