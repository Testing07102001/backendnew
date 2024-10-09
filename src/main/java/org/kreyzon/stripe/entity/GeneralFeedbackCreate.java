package org.kreyzon.stripe.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class GeneralFeedbackCreate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Assuming questions are stored as a list of strings
    @ElementCollection
    private List<String> questions;

    private Long batchId;
    private String moduleName;
    private String dateTaken;
    private String lectureName;

    private boolean createStatus;

    public boolean isCreateStatus() {
        return createStatus;
    }

    public void setCreateStatus(boolean createStatus) {
        this.createStatus = createStatus;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getLectureName() {
        return lectureName;
    }

    public void setLectureName(String lectureName) {
        this.lectureName = lectureName;
    }

}
