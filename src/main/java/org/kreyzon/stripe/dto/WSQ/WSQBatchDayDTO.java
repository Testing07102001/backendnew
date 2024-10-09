package org.kreyzon.stripe.dto.WSQ;

import java.time.LocalDate;

public class WSQBatchDayDTO {
    private Long id;
    private LocalDate date;

    // Constructors, getters, and setters
    public WSQBatchDayDTO(Long id, LocalDate date) {
        this.id = id;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }
}
