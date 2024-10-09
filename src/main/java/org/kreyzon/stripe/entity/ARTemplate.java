package org.kreyzon.stripe.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class ARTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getLearningOutcome() {
        return learningOutcome;
    }

    public void setLearningOutcome(String learningOutcome) {
        this.learningOutcome = learningOutcome;
    }

    public List<CriteriaDetails> getTotalCriteria() {
        return totalCriteria;
    }

    public void setTotalCriteria(List<CriteriaDetails> totalCriteria) {
        this.totalCriteria = totalCriteria;
    }

    private String courseId;
    private String learningOutcome;
    private Long templateId;

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    @ElementCollection
    @CollectionTable(name = "total_criteria", joinColumns = @JoinColumn(name = "template_id"))
    private List<CriteriaDetails> totalCriteria;

}
