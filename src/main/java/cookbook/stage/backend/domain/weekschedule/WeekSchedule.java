package cookbook.stage.backend.domain.weekschedule;

import cookbook.stage.backend.domain.user.User;

import java.util.List;

public record WeekSchedule(WeekScheduleId id, User user, List<DaySchedule> dailyRecipes) {
    public WeekSchedule {
        if (id == null) {
            throw new IllegalArgumentException("WeekSchedule must have an id");
        }
        if (user == null) {
            throw new IllegalArgumentException("WeekSchedule must have a user");
        }
    }

    public WeekSchedule(WeekScheduleId id, List<DaySchedule> dailyRecipes) {
        this(id, null, dailyRecipes);
    }
}
