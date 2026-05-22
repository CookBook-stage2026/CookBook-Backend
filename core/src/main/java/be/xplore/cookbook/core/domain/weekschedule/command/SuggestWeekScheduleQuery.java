package be.xplore.cookbook.core.domain.weekschedule.command;

import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwner;

import java.time.LocalDate;
import java.util.Objects;

public record SuggestWeekScheduleQuery(ScheduleOwner owner, LocalDate weekStartDate, UserId userId) {
    public SuggestWeekScheduleQuery {
        Objects.requireNonNull(owner, "owner cannot be null");
        Objects.requireNonNull(userId, "userId cannot be null");
        Objects.requireNonNull(weekStartDate, "weekStartDate cannot be null");
    }
}
