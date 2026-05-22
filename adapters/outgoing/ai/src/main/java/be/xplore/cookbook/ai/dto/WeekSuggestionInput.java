package be.xplore.cookbook.ai.dto;

import be.xplore.cookbook.core.domain.recipe.RecipeSummary;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;

import java.time.LocalDate;
import java.util.List;

public record WeekSuggestionInput(
        LocalDate weekStartDate,
        List<WeekScheduleInput.ShortRecipeInput> availableRecipes,
        WeekScheduleInput.WeekScheduleContext previousWeek,
        WeekScheduleInput.WeekScheduleContext currentWeek,
        WeekScheduleInput.WeekScheduleContext nextWeek
) {
    public static WeekScheduleInput fromDomain(
            LocalDate weekStartDate,
            List<RecipeSummary> availableRecipes,
            WeekSchedule previousWeek,
            WeekSchedule currentWeek,
            WeekSchedule nextWeek
    ) {
        List<WeekScheduleInput.ShortRecipeInput> recipes = availableRecipes.stream()
                .map(WeekScheduleInput.ShortRecipeInput::fromDomain)
                .toList();

        return new WeekScheduleInput(
                weekStartDate,
                recipes,
                WeekScheduleInput.WeekScheduleContext.fromDomain("previous week", previousWeek),
                WeekScheduleInput.WeekScheduleContext.fromDomain("current week", currentWeek),
                WeekScheduleInput.WeekScheduleContext.fromDomain("next week", nextWeek)
        );
    }
}
