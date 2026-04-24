package cookbook.stage.backend.domain.weekschedule;

import cookbook.stage.backend.domain.user.UserId;

public interface WeekScheduleRepository {
    WeekSchedule save(WeekSchedule schedule);
    WeekSchedule findForUser(UserId userId);
}
