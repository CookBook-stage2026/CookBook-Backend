package be.xplore.cookbook.ai.dto;

import be.xplore.cookbook.core.domain.recipe.RecipeSummary;
import be.xplore.cookbook.core.domain.weekschedule.DaySchedule;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record WeekScheduleInput(
        LocalDate weekStartDate,
        List<ShortRecipeInput> availableRecipes,
        WeekScheduleContext previousWeek,
        WeekScheduleContext currentWeek,
        WeekScheduleContext nextWeek
) {
    public static WeekScheduleInput fromDomain(
            LocalDate weekStartDate,
            List<RecipeSummary> availableRecipes,
            WeekSchedule previousWeek,
            WeekSchedule currentWeek,
            WeekSchedule nextWeek
    ) {
        List<ShortRecipeInput> recipes = availableRecipes.stream()
                .map(ShortRecipeInput::fromDomain)
                .toList();

        return new WeekScheduleInput(
                weekStartDate,
                recipes,
                WeekScheduleContext.fromDomain("previous week", previousWeek),
                WeekScheduleContext.fromDomain("current week", currentWeek),
                WeekScheduleContext.fromDomain("next week", nextWeek)
        );
    }

    public record WeekScheduleContext(
            String weekRole,
            List<DayScheduleInput> schedule
    ) {
        public static WeekScheduleContext fromDomain(String weekRole, WeekSchedule weekSchedule) {
            if (weekSchedule == null) {
                return new WeekScheduleContext(weekRole, List.of());
            }

            Map<DayOfWeek, DaySchedule> scheduledDays = weekSchedule.dailyRecipes().stream()
                    .collect(Collectors.toMap(DaySchedule::day, Function.identity()));

            List<DayScheduleInput> schedule = Arrays.stream(DayOfWeek.values())
                    .map(day -> {
                        DaySchedule daySchedule = scheduledDays.get(day);
                        return daySchedule != null
                                ? DayScheduleInput.fromDomain(daySchedule)
                                : new DayScheduleInput(day, null, null);
                    })
                    .toList();

            return new WeekScheduleContext(weekRole, schedule);
        }
    }

    public record DayScheduleInput(
            DayOfWeek day,
            String recipeName,
            String recipeDescription
    ) {
        public static DayScheduleInput fromDomain(DaySchedule daySchedule) {
            return new DayScheduleInput(
                    daySchedule.day(),
                    daySchedule.recipe().getName(),
                    daySchedule.recipe().getDescription()
            );
        }
    }

    public record ShortRecipeInput(
            java.util.UUID id,
            String name,
            String description
    ) {
        public static ShortRecipeInput fromDomain(RecipeSummary recipe) {
            return new ShortRecipeInput(recipe.id().id(), recipe.name(), recipe.description());
        }
    }
}
