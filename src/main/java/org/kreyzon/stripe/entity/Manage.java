package org.kreyzon.stripe.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Manage {
    @Id
    @GeneratedValue
    private Long id;
    private Long batch_id;
    private String sName;
    private String sEmail;
    private String sPhone;
    private boolean status;
    private LocalDate registered;

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    private String sId;

    public Manage() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBatch_id() {
        return this.batch_id;
    }

    public void setBatch_id(Long batch_id) {
        this.batch_id = batch_id;
    }

    public String getsName() {
        return this.sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsEmail() {
        return this.sEmail;
    }

    public void setsEmail(String sEmail) {
        this.sEmail = sEmail;
    }

    public String getsPhone() {
        return this.sPhone;
    }

    public void setsPhone(String sPhone) {
        this.sPhone = sPhone;
    }

    public boolean isStatus() {
        return this.status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public LocalDate getRegistered() {
        return this.registered;
    }

    public void setRegistered(LocalDate registered) {
        this.registered = registered;
    }


}
