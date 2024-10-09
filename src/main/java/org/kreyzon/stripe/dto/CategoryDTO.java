package org.kreyzon.stripe.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CategoryDTO {
    private Long id;
    private String name;
    private Integer course;
    private LocalDate createdAt;
    private String url;

    public CategoryDTO(Long id, String name, int course, LocalDateTime createdAt, String url) {
    }

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

    public Integer getCourse() {
        return course;
    }

    public void setCourse(Integer course) {
        this.course = course;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
