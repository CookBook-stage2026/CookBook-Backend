package be.xplore.cookbook.core.domain.weekschedule.command;

import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwner;

import java.time.LocalDate;
import java.util.List;

public record CreateWeekScheduleCommand(
        LocalDate weekStartDate,
        List<DayEntry> days,
        ScheduleOwner owner,
        UserId userId) {
}
