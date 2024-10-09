package org.kreyzon.stripe.entity.Intake;

import lombok.Getter;
import lombok.Setter;
import org.kreyzon.stripe.entity.Session;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Map;

@Entity
@Getter
@Setter
public class DateSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private String day; // e.g., "Monday", "Tuesday"

    @ElementCollection
    @CollectionTable(name = "session_details", joinColumns = @JoinColumn(name = "date_session_id"))
    @MapKeyColumn(name = "session_name")
    @Column(name = "session_details")
    private Map<String, Session> sessions;
}

