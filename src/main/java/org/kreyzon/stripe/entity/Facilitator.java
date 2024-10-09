package org.kreyzon.stripe.entity;

import javax.persistence.*;

@Entity
public class Facilitator {
    @Id
    @GeneratedValue
    private Long id;
    private String salutation;
    private String nric;
    private String email;
    private String phone;
    private String educational_level;
    private String qualification;
    private String domain;
    private String gender;
    private String password;
    private String name;
    private String usertype;
    private String prooftype;

    public String getProoftype() {
        return prooftype;
    }

    public void setProoftype(String prooftype) {
        this.prooftype = prooftype;
    }

    public Facilitator() {
    }

    public String getNric() {
        return this.nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSalutation() {
        return this.salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEducational_level() {
        return this.educational_level;
    }

    public void setEducational_level(String educational_level) {
        this.educational_level = educational_level;
    }

    public String getQualification() {
        return this.qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsertype() {
        return this.usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

}
