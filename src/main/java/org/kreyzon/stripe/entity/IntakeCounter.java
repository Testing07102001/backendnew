package org.kreyzon.stripe.entity;

import lombok.Data;

import javax.persistence.*;


@Entity
@Data
public class IntakeCounter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long batchId;


    private int lastGeneratedMonth;
    private int lastGeneratedYear;
    private int previousGlobalIntakeCounter;
    private int globalIntakeCounter;




}
