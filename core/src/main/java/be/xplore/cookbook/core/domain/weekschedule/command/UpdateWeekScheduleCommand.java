package be.xplore.cookbook.core.domain.weekschedule.command;

import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.WeekScheduleId;

import java.util.List;

public record UpdateWeekScheduleCommand(
        WeekScheduleId weekScheduleId,
        UserId userId,
        List<DayEntry> days
) {
    public record DayEntry(RecipeId recipeId, java.time.DayOfWeek day) {
    }
}
