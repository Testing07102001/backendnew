package org.kreyzon.stripe.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
public class TimeTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String intakeId;
    private Long batchId;
    private String course;
    private String type;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "time_table_id")
    private List<ScheduleDateDetail> scheduleDateDetails;

    @ElementCollection
    @CollectionTable(name = "module_details", joinColumns = @JoinColumn(name = "intake_id"))
    private List<ModuleDetail> moduleDetails;
}
