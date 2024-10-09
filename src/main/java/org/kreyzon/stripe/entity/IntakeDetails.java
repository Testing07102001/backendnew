package org.kreyzon.stripe.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
public class IntakeDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String intakeId;

    private Long batchId;
    private String course;
    private String type;
//    private String Venue;

    @ElementCollection
    @CollectionTable(name = "intake_assessors", joinColumns = @JoinColumn(name = "intake_details_id"))
    private List<String> assessors = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "intake_trainers", joinColumns = @JoinColumn(name = "intake_details_id"))
    private List<String> trainers = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleDateDetails> scheduleDateDetails;

    @ElementCollection
    @CollectionTable(name = "intake_module_details", joinColumns = @JoinColumn(name = "intake_details_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "module_id")),
            @AttributeOverride(name = "moduleName", column = @Column(name = "module_name"))
    })
    private List<ModuleDetails> moduleDetails = new ArrayList<>();

    private int currentModule;

    private int lessonsPerWeek;
    private boolean assignment;

}




