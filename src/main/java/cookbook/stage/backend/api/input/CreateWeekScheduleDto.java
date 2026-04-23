package cookbook.stage.backend.api.input;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.UUID;

public record CreateWeekScheduleDto(
        @NotNull int year,
        @NotNull int weekNumber,
        Map<DayOfWeek, UUID> dailyRecipeIds
) {
}
