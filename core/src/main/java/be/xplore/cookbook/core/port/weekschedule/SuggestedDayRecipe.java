package be.xplore.cookbook.core.port.weekschedule;

import be.xplore.cookbook.core.domain.recipe.RecipeId;

import java.time.DayOfWeek;

public record SuggestedDayRecipe(DayOfWeek day, RecipeId recipeId) {
}
