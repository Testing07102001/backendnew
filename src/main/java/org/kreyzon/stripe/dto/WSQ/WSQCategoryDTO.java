package org.kreyzon.stripe.dto.WSQ;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WSQCategoryDTO {
    private Long id;
    private String name;

    // Constructor for convenience
    public WSQCategoryDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}