package be.xplore.cookbook.ai.dto;

import be.xplore.cookbook.core.domain.recipe.RecipeSummary;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

public record DaySuggestionInput(
        DayOfWeek dayToSuggestFor,
        List<WeekScheduleInput> weekSchedules,
        List<ShortRecipeInput> availableRecipes
) {
    public static DaySuggestionInput fromDomain(DayOfWeek dayToSuggestFor, List<RecipeSummary> availableRecipes,
                                                WeekSchedule previousWeek, WeekSchedule currentWeek,
                                                WeekSchedule nextWeek) {
        List<ShortRecipeInput> recipes = availableRecipes.stream()
                .map(ShortRecipeInput::fromDomain)
                .toList();

        List<WeekScheduleInput> schedules = List.of(
                WeekScheduleInput.fromDomain(previousWeek),
                WeekScheduleInput.fromDomain(currentWeek, dayToSuggestFor),
                WeekScheduleInput.fromDomain(nextWeek)
        );

        return new DaySuggestionInput(dayToSuggestFor, schedules, recipes);
    }

    public record ShortRecipeInput(
            UUID id,
            String name,
            String description
    ) {
        public static ShortRecipeInput fromDomain(RecipeSummary recipe) {
            return new ShortRecipeInput(recipe.id().id(), recipe.name(), recipe.description());
        }
    }
}
