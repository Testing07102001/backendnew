package org.kreyzon.stripe.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class Notification {
    @Id
    @GeneratedValue
    private Long id;

    @ElementCollection
    private List<Long> teacherIds; // Corrected method name to match the plural form

    private String subject; // Corrected variable name to start with lowercase
    private String message; // Corrected variable name to start with lowercase

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getTeacherIds() {
        return teacherIds;
    }

    public void setTeacherIds(List<Long> teacherIds) {
        this.teacherIds = teacherIds;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
