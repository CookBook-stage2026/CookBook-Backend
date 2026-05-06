package be.xplore.cookbook.core.domain.weekschedule;

import be.xplore.cookbook.core.domain.user.User;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

public record WeekSchedule(WeekScheduleId id, User user, LocalDate weekStartDate, List<DaySchedule> dailyRecipes) {
    private static final int REMAINING_DAYS_IN_WEEK = 6;

    public WeekSchedule {
        if (id == null) {
            throw new IllegalArgumentException("WeekSchedule must have an id");
        }
        if (user == null) {
            throw new IllegalArgumentException("WeekSchedule must have a user");
        }
        if (weekStartDate == null) {
            throw new IllegalArgumentException("WeekSchedule must have a week start date");
        }
        if (weekStartDate.getDayOfWeek() != DayOfWeek.MONDAY) {
            throw new IllegalArgumentException("Week start date must be a Monday");
        }
        validateNoDuplicateDays(dailyRecipes);
    }

    private static void validateNoDuplicateDays(List<DaySchedule> dailyRecipes) {
        var uniqueDays = new HashSet<DayOfWeek>();
        for (var daySchedule : dailyRecipes) {
            if (daySchedule != null && !uniqueDays.add(daySchedule.day())) {
                throw new IllegalArgumentException("Duplicate day found: " + daySchedule.day());
            }
        }
    }

    public LocalDate weekEndDate() {
        return weekStartDate.plusDays(REMAINING_DAYS_IN_WEEK);
    }
}
