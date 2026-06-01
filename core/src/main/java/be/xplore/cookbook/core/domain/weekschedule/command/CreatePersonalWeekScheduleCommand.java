package be.xplore.cookbook.core.domain.weekschedule.command;

import be.xplore.cookbook.core.domain.user.UserId;

import java.time.LocalDate;
import java.util.List;

public record CreatePersonalWeekScheduleCommand(LocalDate weekStartDate, List<DayEntry> days, UserId userId) {
}
