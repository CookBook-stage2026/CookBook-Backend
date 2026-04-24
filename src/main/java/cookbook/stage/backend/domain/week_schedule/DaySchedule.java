package cookbook.stage.backend.domain.week_schedule;

import cookbook.stage.backend.domain.recipe.Recipe;

import java.time.DayOfWeek;

public record DaySchedule(DayScheduleId id, Recipe recipe, DayOfWeek day) {
}
