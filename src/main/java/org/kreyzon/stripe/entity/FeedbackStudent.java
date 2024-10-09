package org.kreyzon.stripe.entity;

import javax.persistence.*;

@Entity
public class FeedbackStudent {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    @Id
    @GeneratedValue
    private Long id;
    private String questions;
    private int type;
    private Long batchId;
}
