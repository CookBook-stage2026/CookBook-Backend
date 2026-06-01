package be.xplore.cookbook.core.domain.weekschedule.command;

import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.WeekScheduleId;

public record DeleteWeekScheduleCommand(WeekScheduleId scheduleId, UserId userId) {
}
