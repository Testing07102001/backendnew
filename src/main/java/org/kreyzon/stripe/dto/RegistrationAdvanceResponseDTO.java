package org.kreyzon.stripe.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RegistrationAdvanceResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String course;
    private List<ModuleMarkDTO> moduleMarks;

}
