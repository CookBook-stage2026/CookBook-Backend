package be.xplore.cookbook.core.repository;

import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;

public interface WeekScheduleRepository {
    WeekSchedule save(WeekSchedule schedule);

    WeekSchedule findForUser(UserId userId);
}
