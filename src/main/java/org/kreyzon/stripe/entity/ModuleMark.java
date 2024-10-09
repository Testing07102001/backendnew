package org.kreyzon.stripe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter

public class ModuleMark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String moduleName;

    // Define individual fields for marks
    private Integer mark1;
    private Integer mark2;
    private Integer total;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "registration_advance_id")
    private RegistrationAdvance registrationAdvance;
}
