package org.kreyzon.stripe.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.time.LocalDate;

@Getter
@Setter
@Embeddable

public class ModulusDTO {
    private int modulus;
    private LocalDate startDate;
    private LocalDate endDate;


}
