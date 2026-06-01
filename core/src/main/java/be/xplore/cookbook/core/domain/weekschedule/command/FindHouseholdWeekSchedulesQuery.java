package be.xplore.cookbook.core.domain.weekschedule.command;

import be.xplore.cookbook.core.domain.household.HouseholdId;
import be.xplore.cookbook.core.domain.user.UserId;

import java.time.LocalDate;

public record FindHouseholdWeekSchedulesQuery(HouseholdId householdId, LocalDate from, LocalDate to, UserId userId) {
}
