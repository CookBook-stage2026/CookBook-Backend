package be.xplore.cookbook.ai.dto;

import be.xplore.cookbook.core.domain.recipe.RecipeSummary;
import be.xplore.cookbook.core.domain.weekschedule.DaySchedule;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record WeekSuggestionInput(
        LocalDate weekStartDate,
        List<WeekDayEntry> previousWeek,
        List<WeekDayEntry> currentWeek,
        List<WeekDayEntry> nextWeek,
        List<RecipeSummaryEntry> availableRecipes
) {

    public record WeekDayEntry(DayOfWeek day, String recipeName) {
        public static WeekDayEntry fromDomain(DaySchedule daySchedule) {
            return new WeekDayEntry(daySchedule.day(), daySchedule.recipe().getName());
        }
    }

    public record RecipeSummaryEntry(UUID id, String title) {
        public static RecipeSummaryEntry fromDomain(RecipeSummary summary) {
            return new RecipeSummaryEntry(summary.id().id(), summary.name());
        }
    }

    public static WeekSuggestionInput fromDomain(
            LocalDate weekStartDate,
            List<RecipeSummary> availableRecipes,
            WeekSchedule previousWeek,
            WeekSchedule currentWeek,
            WeekSchedule nextWeek
    ) {
        return new WeekSuggestionInput(
                weekStartDate,
                previousWeek.dailyRecipes().stream().map(WeekDayEntry::fromDomain).toList(),
                currentWeek.dailyRecipes().stream().map(WeekDayEntry::fromDomain).toList(),
                nextWeek.dailyRecipes().stream().map(WeekDayEntry::fromDomain).toList(),
                availableRecipes.stream().map(RecipeSummaryEntry::fromDomain).toList()
        );
    }
}
