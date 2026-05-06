package be.xplore.cookbook.core.domain.weekschedule.command;

import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.user.UserId;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public record CreateWeekScheduleCommand(
        LocalDate weekStartDate,
        List<DayEntry> days,
        UserId userId) {

    public record DayEntry(RecipeId recipeId, DayOfWeek day) {
    }
}
