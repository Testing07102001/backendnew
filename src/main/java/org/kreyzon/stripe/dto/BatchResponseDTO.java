package org.kreyzon.stripe.dto;

import org.kreyzon.stripe.entity.Day;

import java.time.LocalDate;
import java.util.List;

public class BatchResponseDTO {
    private Long id;
    private Integer registrationFee;
    private Integer courseFee;
    private String name;
    private String type;
    private String course;
    private String batchcode;
    private String onlineclasslink;
    private String venue;
    private Integer currentModule;
    private LocalDate startDate;
    private String courseType;
    private String courseImage;
    private List<ModuleDetailDTO> moduleDetails;
    private List<String> assessors;
    private List<String> trainers;
    private List<Day> days;
    private String intakeId;
    private String reference;
    private String description;
    private String outcome;
    private String requirement;
    private CategoryDTO category;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRegistrationFee() {
        return registrationFee;
    }

    public void setRegistrationFee(Integer registrationFee) {
        this.registrationFee = registrationFee;
    }

    public Integer getCourseFee() {
        return courseFee;
    }

    public void setCourseFee(Integer courseFee) {
        this.courseFee = courseFee;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getBatchcode() {
        return batchcode;
    }

    public void setBatchcode(String batchcode) {
        this.batchcode = batchcode;
    }

    public String getOnlineclasslink() {
        return onlineclasslink;
    }

    public void setOnlineclasslink(String onlineclasslink) {
        this.onlineclasslink = onlineclasslink;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Integer getCurrentModule() {
        return currentModule;
    }

    public void setCurrentModule(Integer currentModule) {
        this.currentModule = currentModule;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getCourseImage() {
        return courseImage;
    }

    public void setCourseImage(String courseImage) {
        this.courseImage = courseImage;
    }

    public List<ModuleDetailDTO> getModuleDetails() {
        return moduleDetails;
    }

    public void setModuleDetails(List<ModuleDetailDTO> moduleDetails) {
        this.moduleDetails = moduleDetails;
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

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }

    public String getIntakeId() {
        return intakeId;
    }

    public void setIntakeId(String intakeId) {
        this.intakeId = intakeId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }
}
