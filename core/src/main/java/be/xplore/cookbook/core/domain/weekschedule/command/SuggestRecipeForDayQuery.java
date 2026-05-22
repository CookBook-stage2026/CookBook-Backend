package be.xplore.cookbook.core.domain.weekschedule.command;

import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwner;

import java.time.LocalDate;

public record SuggestRecipeForDayQuery(ScheduleOwner owner, LocalDate dayToSuggestFor, UserId userId) {
}
