package be.xplore.cookbook.rest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateWeekScheduleDto(
        @Valid @NotNull List<CreateDayScheduleDto> days
) {
}
