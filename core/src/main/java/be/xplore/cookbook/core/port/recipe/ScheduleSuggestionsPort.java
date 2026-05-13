package be.xplore.cookbook.core.port.recipe;

import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.recipe.RecipeSummary;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;

import java.time.DayOfWeek;
import java.util.List;

public interface ScheduleSuggestionsPort {
    RecipeId suggestRecipeForDay(DayOfWeek dayToSuggestFor, List<WeekSchedule> weekSchedules,
                                 List<RecipeSummary> availableRecipes);
}
