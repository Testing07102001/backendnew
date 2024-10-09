package org.kreyzon.stripe.entity;

import lombok.Getter;
import lombok.Setter;
import org.kreyzon.stripe.entity.Intake.DaySession;
import org.kreyzon.stripe.entity.Intake.Week;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "schedule_date_details")
@Getter
@Setter
public class ScheduleDateDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private int startModulus;
    private LocalDate startDate;
    private LocalDate endDate;
    private String trainer;
    private String venue;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "schedule_id")
    private List<Week> weeks;

}