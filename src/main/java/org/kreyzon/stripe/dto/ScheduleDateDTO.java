package org.kreyzon.stripe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Data
@AllArgsConstructor
public class ScheduleDateDTO {
    private int startModulus;
    private LocalDate startDate;
    private LocalDate endDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleDateDTO that = (ScheduleDateDTO) o;
        return startModulus == that.startModulus &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startModulus, startDate, endDate);
    }
    public ScheduleDateDTO(String format, String format1, int startModulus) {
    }
}
