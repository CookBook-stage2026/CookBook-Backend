package be.xplore.cookbook.core.domain.weekschedule.command;

import be.xplore.cookbook.core.domain.user.UserId;

import java.time.LocalDate;

public record FindWeekSchedulesByUserQuery(UserId userId, LocalDate from, LocalDate to) {
    public boolean hasDateRange() {
        return from != null && to != null;
    }
}
