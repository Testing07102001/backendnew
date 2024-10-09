package org.kreyzon.stripe.dto.WSQ;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WSQCourseDTO {
    private Long id;
    private String name;


    public WSQCourseDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
