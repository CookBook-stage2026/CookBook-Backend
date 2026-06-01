package be.xplore.cookbook.core.domain.weekschedule;

import be.xplore.cookbook.core.domain.household.HouseholdId;
import be.xplore.cookbook.core.domain.user.UserId;

import java.util.UUID;

public record ScheduleOwner(UUID ownerId, ScheduleOwnerType ownerType) {

    public ScheduleOwner {
        if (ownerId == null) {
            throw new IllegalArgumentException("ScheduleOwner must have an ownerId");
        }
        if (ownerType == null) {
            throw new IllegalArgumentException("ScheduleOwner must have an ownerType");
        }
    }

    public static ScheduleOwner forUser(UserId userId) {
        return new ScheduleOwner(userId.id(), ScheduleOwnerType.PERSONAL);
    }

    public static ScheduleOwner forHousehold(HouseholdId householdId) {
        return new ScheduleOwner(householdId.id(), ScheduleOwnerType.HOUSEHOLD);
    }

    public boolean isPersonal() {
        return ownerType == ScheduleOwnerType.PERSONAL;
    }

    public boolean isHousehold() {
        return ownerType == ScheduleOwnerType.HOUSEHOLD;
    }
}
