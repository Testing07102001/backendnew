package org.kreyzon.stripe.dto.WSQ;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WSQCourseResponseDTO {
    private int id;
    private String name;
    private String fee;
    private List<String> languages;
    private CategoryResponseDTO category; // Include CategoryResponseDTO

    // Constructor for easy conversion from WSQCourse
    public WSQCourseResponseDTO(int id,String name, String fee, List<String> languages, CategoryResponseDTO category) {
        this.id=id;
        this.name = name;
        this.fee = fee;
        this.languages = languages;
        this.category = category; // Set the category
    }
}
