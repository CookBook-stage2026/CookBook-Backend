package cookbook.stage.backend.api.input;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.util.UUID;


public record CreateDayScheduleDto(@NotNull UUID recipeId, @NotNull DayOfWeek day) {
}
