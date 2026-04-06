package com.appointment.dto.ehr;

import com.appointment.enums.HistoryType;
import com.appointment.enums.Relationship;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class HistoryDto {

    private UUID uuid;

    private String name;

    private LocalDate date;

    private String note;

    private LocalDate recordedDate;

    private Integer onSetAge;

    @NotNull(message = "History type is required")
    private HistoryType historyType;

    private Relationship relation;

    @NotBlank(message = "Condition is required")
    private String condition;

    private String notes;

    private LocalDateTime createdAt;
}
