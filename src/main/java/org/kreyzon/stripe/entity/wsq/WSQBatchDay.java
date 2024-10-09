package org.kreyzon.stripe.entity.wsq;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class WSQBatchDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    // Other fields can be added as necessary (e.g., start time, end time)

    // Many batch days can belong to one batch
    @ManyToOne
    @JoinColumn(name = "batch_id")
    @JsonIgnore // Prevent serialization of batch
    private WSQBatch batch;
    @ElementCollection
    private List<WSQSession> session = new ArrayList<>();


    // Add a method to get sessions as a dynamic map for serialization
}
