package be.xplore.cookbook.ai.dto;

import be.xplore.cookbook.core.domain.recipe.RecipeSummary;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;

import java.time.LocalDate;
import java.util.List;

public record WeekSuggestionInput(
        LocalDate weekStartDate,
        List<ShortRecipeInput> availableRecipes,
        WeekScheduleInput previousWeek,
        WeekScheduleInput currentWeek,
        WeekScheduleInput nextWeek
) {
    public static WeekSuggestionInput fromDomain(
            LocalDate weekStartDate,
            List<RecipeSummary> availableRecipes,
            WeekSchedule previousWeek,
            WeekSchedule currentWeek,
            WeekSchedule nextWeek
    ) {
        List<ShortRecipeInput> recipes = availableRecipes.stream()
                .map(ShortRecipeInput::fromDomain)
                .toList();

        return new WeekSuggestionInput(
                weekStartDate,
                recipes,
                WeekScheduleInput.fromDomain("previous week", previousWeek),
                WeekScheduleInput.fromDomain("current week", currentWeek),
                WeekScheduleInput.fromDomain("next week", nextWeek)
        );
    }
}
