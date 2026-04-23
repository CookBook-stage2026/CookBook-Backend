package cookbook.stage.backend.domain.user;

import cookbook.stage.backend.domain.recipe.Recipe;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public record WeekSchedule(WeekScheduleId id, int year, int weekNumber, Map<DayOfWeek, Recipe> dailyRecipes) {

    private static final int MAX_DAYS_IN_WEEK = 7;
    private static final int MAX_WEEKS_IN_ADVANCE = 8;

    public WeekSchedule(WeekScheduleId id, int year, int weekNumber, Map<DayOfWeek, Recipe> dailyRecipes) {
        if (id == null) {
            throw new IllegalArgumentException("WeekSchedule must have an id");
        }
        if (weekNumber < 1 || weekNumber > MAX_DAYS_IN_WEEK * MAX_WEEKS_IN_ADVANCE) {
            throw new IllegalArgumentException("Week number must be between 1 and 53");
        }
        this.id = id;
        this.year = year;
        this.weekNumber = weekNumber;
        this.dailyRecipes = new EnumMap<>(DayOfWeek.class);
        this.dailyRecipes.putAll(dailyRecipes);
    }

    public void assignRecipe(DayOfWeek day, Recipe recipe) {
        if (day == null) {
            throw new IllegalArgumentException("Day cannot be null");
        }
        if (recipe == null) {
            throw new IllegalArgumentException("Recipe cannot be null — use skipDay() to remove a recipe");
        }
        dailyRecipes.put(day, recipe);
    }

    public void skipDay(DayOfWeek day) {
        if (day == null) {
            throw new IllegalArgumentException("Day cannot be null");
        }
        dailyRecipes.remove(day);
    }

    public Optional<Recipe> getRecipeForDay(DayOfWeek day) {
        return Optional.ofNullable(dailyRecipes.get(day));
    }

    @Override
    public Map<DayOfWeek, Recipe> dailyRecipes() {
        return Collections.unmodifiableMap(dailyRecipes);
    }
}
