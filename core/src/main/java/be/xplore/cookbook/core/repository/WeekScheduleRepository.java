package be.xplore.cookbook.core.repository;

import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;
import be.xplore.cookbook.core.domain.weekschedule.WeekScheduleId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeekScheduleRepository {
    WeekSchedule save(WeekSchedule schedule);
    List<WeekSchedule> findAllByUserId(UserId userId);
    List<WeekSchedule> findAllByUserIdAndDateRange(UserId userId, LocalDate from, LocalDate to);
    Optional<WeekSchedule> findById(WeekScheduleId id);
    Optional<WeekSchedule> findByUserIdAndWeekStartDate(UserId userId, LocalDate weekStartDate);
    void deleteById(WeekScheduleId id);
}
