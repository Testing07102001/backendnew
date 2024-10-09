package org.kreyzon.stripe.entity;

import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class ModuleDetails {
    private Long id;
    private String moduleName;
}
