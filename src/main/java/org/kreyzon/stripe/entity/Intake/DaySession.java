package org.kreyzon.stripe.entity.Intake;

import lombok.*;
import org.kreyzon.stripe.entity.Session;

import javax.persistence.*;
import java.util.Map;

@Getter
@Setter
@Entity
public class DaySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    private Map<String, Session> sessions;

}
