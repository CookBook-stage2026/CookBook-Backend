package be.xplore.cookbook.core.repository;

import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;
import be.xplore.cookbook.core.domain.user.UserId;

public interface WeekScheduleRepository {
    WeekSchedule save(WeekSchedule schedule);
    WeekSchedule findForUser(UserId userId);
}
