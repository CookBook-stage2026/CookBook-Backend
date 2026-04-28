package be.xplore.cookbook.core.domain.weekschedule;

import be.xplore.cookbook.core.domain.user.User;

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.List;

public record WeekSchedule(WeekScheduleId id, User user, List<DaySchedule> dailyRecipes) {
    public WeekSchedule {
        if (id == null) {
            throw new IllegalArgumentException("WeekSchedule must have an id");
        }
        if (user == null) {
            throw new IllegalArgumentException("WeekSchedule must have a user");
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
}
