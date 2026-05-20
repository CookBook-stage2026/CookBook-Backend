package be.xplore.cookbook.core.domain.weekschedule.command;

import be.xplore.cookbook.core.domain.user.UserId;

import java.time.LocalDate;

public record SuggestRecipeForDayQuery(LocalDate dayToSuggestFor, UserId userId) {
}
