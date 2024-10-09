package org.kreyzon.stripe.entity;

import javax.persistence.*;

@Entity
public class Assessment {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String course;
    private String batch;
    private int duration;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    private String start;
    private String end_time;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private Long courseId;
    private String description;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    private Long categoryId;

// Constructors, getters, and setters
// ...


    // Getter and Setter for 'id'
    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    // Getter and Setter for 'name'
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    // Getter and Setter for 'course'
    public String getCourse() {
        return course;
    }


    public void setCourse(String course) {
        this.course = course;
    }


    // Getter and Setter for 'batch'
    public String getBatch() {
        return batch;
    }


    public void setBatch(String batch) {
        this.batch = batch;
    }


    // Getter and Setter for 'duration'
    public int getDuration() {
        return duration;
    }


    public void setDuration(int duration) {
        this.duration = duration;
    }

}
