package be.xplore.cookbook.core.domain.household.exception;

import be.xplore.cookbook.core.domain.exception.NotFoundException;
import be.xplore.cookbook.core.domain.household.HouseholdId;

public class HouseholdNotFoundException extends NotFoundException {
    public HouseholdNotFoundException(HouseholdId householdId) {
        super("Household with id " + householdId + " not found.");
    }
}
