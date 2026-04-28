package cookbook.stage.backend.domain.weekschedule;

import cookbook.stage.backend.domain.recipe.Recipe;

import java.time.DayOfWeek;

public record DaySchedule(DayScheduleId id, Recipe recipe, DayOfWeek day) {
}
