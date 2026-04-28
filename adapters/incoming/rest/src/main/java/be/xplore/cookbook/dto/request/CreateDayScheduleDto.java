package be.xplore.cookbook.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.util.UUID;


public record CreateDayScheduleDto(@NotNull UUID recipeId, @NotNull DayOfWeek day) {
}
