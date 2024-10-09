package org.kreyzon.stripe.dto;

public class ModuleDetailDTO {
    private Long id;
    private String moduleName;

    public ModuleDetailDTO(Long id, String moduleName) {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}
