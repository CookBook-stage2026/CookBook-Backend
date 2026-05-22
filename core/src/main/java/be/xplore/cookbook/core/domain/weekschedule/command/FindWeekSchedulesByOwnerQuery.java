package be.xplore.cookbook.core.domain.weekschedule.command;

import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwner;

import java.time.LocalDate;

public record FindWeekSchedulesByOwnerQuery(ScheduleOwner owner, LocalDate from, LocalDate to, UserId userId) {
    public boolean hasDateRange() {
        return from != null && to != null;
    }
}
