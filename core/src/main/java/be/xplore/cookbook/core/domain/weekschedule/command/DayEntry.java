package be.xplore.cookbook.core.domain.weekschedule.command;

import be.xplore.cookbook.core.domain.recipe.RecipeId;

import java.time.DayOfWeek;

public record DayEntry(RecipeId recipeId, DayOfWeek day) {
}
