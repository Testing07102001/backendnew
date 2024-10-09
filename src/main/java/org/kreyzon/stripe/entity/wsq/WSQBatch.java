package org.kreyzon.stripe.entity.wsq;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class WSQBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int intakeSize;

    // Many batches can belong to one course
    @ManyToOne
    @JoinColumn(name = "course_id")
    private WSQCourse course;


    // Many batches can be held in one venue
    @ManyToOne
    @JoinColumn(name = "venue_id")
    private WSQVenue venue;
    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WSQBatchDay> days = new ArrayList<>();
}