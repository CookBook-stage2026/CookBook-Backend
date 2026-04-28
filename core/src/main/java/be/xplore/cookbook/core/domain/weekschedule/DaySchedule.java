package be.xplore.cookbook.core.domain.weekschedule;

import be.xplore.cookbook.core.domain.recipe.Recipe;

import java.time.DayOfWeek;

public record DaySchedule(DayScheduleId id, Recipe recipe, DayOfWeek day) {
}
