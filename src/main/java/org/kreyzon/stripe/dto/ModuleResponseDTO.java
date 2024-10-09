package org.kreyzon.stripe.dto;

public class ModuleResponseDTO {
    private int currentModule;
    private String moduleName;

    // Constructors, Getters, Setters
    public ModuleResponseDTO(int currentModule, String moduleName) {
        this.currentModule = currentModule;
        this.moduleName = moduleName;
    }

    public int getCurrentModule() {
        return currentModule;
    }

    public void setCurrentModule(int currentModule) {
        this.currentModule = currentModule;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}
