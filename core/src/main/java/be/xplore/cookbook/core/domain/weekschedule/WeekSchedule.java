package be.xplore.cookbook.core.domain.weekschedule;

import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.user.UserId;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public record WeekSchedule(
        WeekScheduleId id,
        ScheduleOwner owner,
        LocalDate weekStartDate,
        List<DaySchedule> dailyRecipes
) {
    private static final int REMAINING_DAYS_IN_WEEK = 6;

    public WeekSchedule {
        if (id == null) {
            throw new IllegalArgumentException("WeekSchedule must have an id");
        }
        if (owner == null) {
            throw new IllegalArgumentException("WeekSchedule must have an owner");
        }
        if (weekStartDate == null) {
            throw new IllegalArgumentException("WeekSchedule must have a week start date");
        }
        if (weekStartDate.getDayOfWeek() != DayOfWeek.MONDAY) {
            throw new IllegalArgumentException("Week start date must be a Monday");
        }
        validateNoDuplicateDays(dailyRecipes);
    }

    public static WeekSchedule empty(ScheduleOwner owner, LocalDate weekStartDate) {
        return new WeekSchedule(
                WeekScheduleId.create(),
                owner,
                weekStartDate,
                List.of()
        );
    }

    public LocalDate weekEndDate() {
        return weekStartDate.plusDays(REMAINING_DAYS_IN_WEEK);
    }

    public WeekSchedule assignRecipe(DayOfWeek day, Recipe recipe) {
        List<DaySchedule> updated = new ArrayList<>(dailyRecipes);

        boolean replaced = false;

        for (int i = 0; i < updated.size(); i++) {
            if (updated.get(i).day() == day) {
                updated.set(i, new DaySchedule(updated.get(i).id(), recipe, day));
                replaced = true;
            }
        }

        if (!replaced) {
            updated.add(new DaySchedule(DayScheduleId.create(), recipe, day));
        }

        return new WeekSchedule(id, owner, weekStartDate, List.copyOf(updated));
    }

    private static void validateNoDuplicateDays(List<DaySchedule> dailyRecipes) {
        var uniqueDays = new HashSet<DayOfWeek>();
        for (var daySchedule : dailyRecipes) {
            if (daySchedule != null && !uniqueDays.add(daySchedule.day())) {
                throw new IllegalArgumentException("Duplicate day found: " + daySchedule.day());
            }
        }
    }

    public WeekSchedule removeByRecipeId(RecipeId recipeId) {
        List<DaySchedule> updated = dailyRecipes.stream()
                .filter(day -> !day.recipe().getId().equals(recipeId))
                .toList();

        return new WeekSchedule(id, owner, weekStartDate, updated);
    }

    public WeekSchedule removeByUserId(UserId userId) {
        List<DaySchedule> updated = dailyRecipes.stream()
                .filter(day -> !day.recipe().getUser().id().equals(userId))
                .toList();

        return new WeekSchedule(id, owner, weekStartDate, updated);
    }
}
