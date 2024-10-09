package org.kreyzon.stripe.entity;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.List;

@Entity
public class ARAssessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cId;
    private Long bId;

    public Long getbId() {
        return bId;
    }

    public void setbId(Long bId) {
        this.bId = bId;
    }

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    private String skillCode;
    private String skill;
    private String sNric;
    @ElementCollection
    private List<String> assessors;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getcId() {
        return cId;
    }

    public void setcId(Long cId) {
        this.cId = cId;
    }

    public String getSkillCode() {
        return skillCode;
    }

    public void setSkillCode(String skillCode) {
        this.skillCode = skillCode;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getsNric() {
        return sNric;
    }

    public void setsNric(String sNric) {
        this.sNric = sNric;
    }

    public List<String> getAssessors() {
        return assessors;
    }

    public void setAssessors(List<String> assessors) {
        this.assessors = assessors;
    }

    public List<String> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<String> trainers) {
        this.trainers = trainers;
    }

    public String getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(String assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public String getPc_nyc() {
        return pc_nyc;
    }

    public void setPc_nyc(String pc_nyc) {
        this.pc_nyc = pc_nyc;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String geteSign() {
        return eSign;
    }

    public void seteSign(String eSign) {
        this.eSign = eSign;
    }

    public List<ARTemplate> getTemplates() {
        return templates;
    }

    public void setTemplates(List<ARTemplate> templates) {
        this.templates = templates;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    @ElementCollection
    private List<String> trainers;
    private String assessmentDate;
    private String pc_nyc;
    private String feedback;
    private String eSign;
    @ElementCollection
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "assessment_id")
    private List<ARTemplate> templates;
    private String sname;
    private Long sid;
}
