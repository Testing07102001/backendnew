package org.kreyzon.stripe.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class BatchDTO {
    private String studentName;
    private String studentEmail;
    private Long batchId;
    private int startModulus;
    private LocalDate enrollmentDate;
    private List<ModulusDTO> moduli;
}
