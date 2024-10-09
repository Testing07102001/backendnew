package org.kreyzon.stripe.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Batch {
    @Id
    @GeneratedValue
    private Long id;
    private Long registrationFee;
    private Long courseFee;
    private String name;
    private String type;
    private String course;

    private String batchcode;
    @Lob
    private String onlineclasslink;
    @Lob
    private String venue;

    private int currentModule;

    private LocalDate startDate;
    private String courseType;
    private String courseImage;

    @ElementCollection
    @CollectionTable(name = "batch_module_details", joinColumns = @JoinColumn(name = "batch_id"))
    private List<ModuleDetails> moduleDetails = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "batch_assessors", joinColumns = @JoinColumn(name = "batch_id"))
    private List<String> assessors = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "batch_trainers", joinColumns = @JoinColumn(name = "batch_id"))
    private List<String> trainers = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "batch_id")
    private List<Day> days = new ArrayList<>();

    @Column(name = "intake_id")
    private String intakeId;
    private String reference;
    @Lob
    private String description;
    @Lob
    private String outcome;
    @Lob
    private String requirement;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Categories category;

    private int lessonsPerWeek;
    private boolean assignment;

}
