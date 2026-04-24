package cookbook.stage.backend.domain.week_schedule;

import cookbook.stage.backend.domain.user.User;

import java.util.List;

public record WeekSchedule(WeekScheduleId id, User user, List<DaySchedule> dailyRecipes) {
    public WeekSchedule {
        if (id == null) {
            throw new IllegalArgumentException("WeekSchedule must have an id");
        }
    }
}
