package be.xplore.cookbook.rest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CreateWeekScheduleDto(
        @NotNull LocalDate weekStartDate,
        @Valid @NotNull List<CreateDayScheduleDto> days
) {
}
