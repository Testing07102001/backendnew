package org.kreyzon.stripe.dto.WSQ;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CategoryResponseDTO {
    private Long id; // Assuming this is the ID of the category
    private String name; // Include other fields as needed
    private LocalDateTime createdTime; // Add the createdTime field

    // Constructor
    public CategoryResponseDTO(Long id, String name, LocalDateTime createdTime) {
        this.id = id;
        this.name = name;
        this.createdTime = createdTime;
    }
}
