package org.kreyzon.stripe.entity;

import javax.persistence.*;

@Entity
public class UploadCurriculum {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE
    )

    private Long id;
    private String sectionName;
    private String selectCurriculum;
    private boolean publicReview;
    private String type;
    private boolean seen;

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSelectCurriculum() {
        return selectCurriculum;
    }

    public void setSelectCurriculum(String selectCurriculum) {
        this.selectCurriculum = selectCurriculum;
    }

    public boolean isPublicReview() {
        return publicReview;
    }

    public void setPublicReview(boolean publicReview) {
        this.publicReview = publicReview;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFor_access() {
        return for_access;
    }

    public void setFor_access(String for_access) {
        this.for_access = for_access;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public long getCurriculum_id() {
        return curriculum_id;
    }

    public void setCurriculum_id(long curriculum_id) {
        this.curriculum_id = curriculum_id;
    }

    public long getCourse_id() {
        return course_id;
    }

    public void setCourse_id(long course_id) {
        this.course_id = course_id;
    }

    private String for_access;
    private String fileUrl;
    private long curriculum_id;
    private long course_id;

}
