package org.kreyzon.stripe.entity;

import javax.persistence.*;
import java.util.Map;

@Entity
public class Template {
    @Id
    @GeneratedValue
    private Long id;
    private String type;
    private String courseId;
    private String learningOutcome;

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

    public Map<String, String> getQuestLabels() {
        return questLabels;
    }

    public void setQuestLabels(Map<String, String> questLabels) {
        this.questLabels = questLabels;
    }

    public Map<String, String> getCriteria() {
        return criteria;
    }

    public void setCriteria(Map<String, String> criteria) {
        this.criteria = criteria;
    }

    @ElementCollection
    @CollectionTable(name = "quest_labels", joinColumns = @JoinColumn(name = "template_id"))
    @MapKeyColumn(name = "label_key")
    @Column(name = "label_value")
    private Map<String, String> questLabels;

    @ElementCollection
    @CollectionTable(name = "criteria", joinColumns = @JoinColumn(name = "template_id"))
    @MapKeyColumn(name = "criteria_key")
    @Column(name = "criteria_value")
    private Map<String, String> criteria;
}
