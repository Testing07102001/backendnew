package org.kreyzon.stripe.entity;

import javax.persistence.*;

@Entity
public class InvoiceSettings {
    @Id
    @GeneratedValue
    private Long id;


    private String name;
    private String type;
    private String calculator;
    private int value;




    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public String getCalculator() {
        return calculator;
    }


    public void setCalculator(String calculator) {
        this.calculator = calculator;
    }


    public int getValue() {
        return value;
    }


    public void setValue(int value) {
        this.value = value;
    }


}
