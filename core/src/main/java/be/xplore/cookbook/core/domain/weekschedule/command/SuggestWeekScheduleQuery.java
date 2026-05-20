package be.xplore.cookbook.core.domain.weekschedule.command;

import be.xplore.cookbook.core.domain.user.UserId;

import java.time.LocalDate;
import java.util.Objects;

public record SuggestWeekScheduleQuery(UserId userId, LocalDate weekStartDate) {
    public SuggestWeekScheduleQuery {
        Objects.requireNonNull(userId, "userId cannot be null");
        Objects.requireNonNull(weekStartDate, "weekStartDate cannot be null");
    }
}
