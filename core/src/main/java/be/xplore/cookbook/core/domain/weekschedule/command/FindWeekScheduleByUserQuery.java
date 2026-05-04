package be.xplore.cookbook.core.domain.weekschedule.command;

import be.xplore.cookbook.core.domain.user.UserId;

public record FindWeekScheduleByUserQuery(UserId userId) {
}
