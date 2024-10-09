package org.kreyzon.stripe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.kreyzon.stripe.entity.ModuleDetails;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BatchSummaryDTO {
    private Long id;
    private String type;
    private String course;
    private String batchcode;
    private String venue;
    private List<ModuleDetails> moduleDetails;
    private int currentModule;
    private List<String> intakeIds;
    private long activeStudentCount;


}
