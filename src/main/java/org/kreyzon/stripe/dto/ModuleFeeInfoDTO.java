package org.kreyzon.stripe.dto;

import java.time.LocalDate;

public class ModuleFeeInfoDTO {
    private LocalDate startModuleDate;
    private LocalDate endModuleDate;
    private Long courseFeeRemaining;
    private Long registrationFeeRemaining;

    public LocalDate getStartModuleDate() {
        return startModuleDate;
    }

    public void setStartModuleDate(LocalDate startModuleDate) {
        this.startModuleDate = startModuleDate;
    }

    public LocalDate getEndModuleDate() {
        return endModuleDate;
    }

    public void setEndModuleDate(LocalDate endModuleDate) {
        this.endModuleDate = endModuleDate;
    }

    public Long getCourseFeeRemaining() {
        return courseFeeRemaining;
    }

    public void setCourseFeeRemaining(Long courseFeeRemaining) {
        this.courseFeeRemaining = courseFeeRemaining;
    }

    public Long getRegistrationFeeRemaining() {
        return registrationFeeRemaining;
    }

    public void setRegistrationFeeRemaining(Long registrationFeeRemaining) {
        this.registrationFeeRemaining = registrationFeeRemaining;
    }
}
