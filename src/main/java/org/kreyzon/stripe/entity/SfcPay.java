package org.kreyzon.stripe.entity;

import javax.persistence.*;

@Entity
public class SfcPay {
    @Id
    @GeneratedValue
    private Long id;


    private String batch;
    private String student;
    private int fee;
    private String document;


// Constructors (you might already have these)


// Default constructor


    // Getters and Setters
    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getBatch() {
        return batch;
    }


    public void setBatch(String batch) {
        this.batch = batch;
    }


    public String getStudent() {
        return student;
    }


    public void setStudent(String student) {
        this.student = student;
    }


    public int getFee() {
        return fee;
    }


    public void setFee(int fee) {
        this.fee = fee;
    }


    public String getDocument() {
        return document;
    }


    public void setDocument(String document) {
        this.document = document;
    }

}
