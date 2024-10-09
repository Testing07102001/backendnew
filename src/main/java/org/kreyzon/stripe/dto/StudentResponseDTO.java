package org.kreyzon.stripe.dto;

import org.kreyzon.stripe.entity.ModuleMark;

import java.util.List;

public class StudentResponseDTO {
    private String studentName;
    private Long id;
    private String nric;
    private String intakeId;
    private String course;
    private List<ModuleMarksDTO> moduleMarks;

    // Getters and Setters
    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public String getIntakeId() {
        return intakeId;
    }

    public void setIntakeId(String intakeId) {
        this.intakeId = intakeId;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ModuleMarksDTO> getModuleMarks() {
        return moduleMarks;
    }

    public void setModuleMarks(List<ModuleMarksDTO> moduleMarks) {
        this.moduleMarks = moduleMarks;
    }
}
