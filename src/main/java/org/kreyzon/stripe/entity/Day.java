package org.kreyzon.stripe.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Entity
public class Day {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String day; // e.g., "Sunday", "Saturday"

    @ElementCollection
    @CollectionTable(name = "day_sessions", joinColumns = @JoinColumn(name = "day_id"))
    @MapKeyColumn(name = "session_name")
    @Column(name = "session_details")
    private Map<String, Session> sessions = new HashMap<>();
}
