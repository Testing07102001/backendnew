package org.kreyzon.stripe.dto.WSQ;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class WSQCourseRequestDTO {
    private String name;
    private String fee;
    private List<String> languages;
    private Long categoryId;  // This will help us link the course to a category
}
