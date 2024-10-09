package org.kreyzon.stripe.entity;

import javax.persistence.*;
import java.util.Map;

@Entity
public class BatchDay {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, String> getMorningFrom() {
        return morningFrom;
    }

    public void setMorningFrom(Map<String, String> morningFrom) {
        this.morningFrom = morningFrom;
    }

    public Map<String, String> getMorningTo() {
        return morningTo;
    }

    public void setMorningTo(Map<String, String> morningTo) {
        this.morningTo = morningTo;
    }

    public Map<String, String> getEveningFrom() {
        return eveningFrom;
    }

    public void setEveningFrom(Map<String, String> eveningFrom) {
        this.eveningFrom = eveningFrom;
    }

    public Map<String, String> getEveningTo() {
        return eveningTo;
    }

    public void setEveningTo(Map<String, String> eveningTo) {
        this.eveningTo = eveningTo;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @MapKeyColumn(name = "time_slot", columnDefinition = "VARCHAR(255) DEFAULT 'default_value'")
    @Column(name = "morning_from")
    private Map<String, String> morningFrom;

    @ElementCollection
    @MapKeyColumn(name = "time_slot", columnDefinition = "VARCHAR(255) DEFAULT 'default_value'")
    @Column(name = "morning_to")
    private Map<String, String> morningTo;


    @ElementCollection
    @MapKeyColumn(name = "time_slot", columnDefinition = "VARCHAR(255) DEFAULT 'default_value'")
    @Column(name = "evening_from")
    private Map<String, String> eveningFrom;

    @ElementCollection
    @MapKeyColumn(name = "time_slot", columnDefinition = "VARCHAR(255) DEFAULT 'default_value'")
    @Column(name = "evening_to")
    private Map<String, String> eveningTo;


    public String getBatchDate() {
        return batchDate;
    }

    public void setBatchDate(String batchDate) {
        this.batchDate = batchDate;
    }

    private String batchDate;

    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    private String assessment;
}
