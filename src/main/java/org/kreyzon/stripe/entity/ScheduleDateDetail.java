package org.kreyzon.stripe.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Embeddable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
public class ScheduleDateDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int startModulus;
    private LocalDate startDate;
    private LocalDate endDate;
    private String trainer;
    private String venue;
    @ElementCollection
    @CollectionTable(name = "schedule_days", joinColumns = @JoinColumn(name = "schedule_date_detail_id"))
    @Column(name = "day")
    private List<String> days;

    private LocalTime startTime;
    private LocalTime endTime;




}
