package org.kreyzon.stripe.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;


@Getter
@Setter
@Embeddable
public class MarkSheet {

    private String level1;
    private String level2;
    private String level3;
    private String level4;
}
