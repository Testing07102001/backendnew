package org.kreyzon.stripe.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuleMarksDTO {
    private String moduleName;
    private Integer mark1;
    private Integer mark2;
    private Integer total;
    public ModuleMarksDTO(String moduleName, Integer mark1, Integer mark2,Integer total) {
        this.moduleName = moduleName;
        this.mark1 = mark1;
        this.mark2 = mark2;
        this.total = total;
    }

    public ModuleMarksDTO() {
    }
}
