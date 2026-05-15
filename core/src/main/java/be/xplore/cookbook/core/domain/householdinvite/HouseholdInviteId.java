package be.xplore.cookbook.core.domain.householdinvite;

import java.util.UUID;

public record HouseholdInviteId(UUID id) {
    public static HouseholdInviteId create() {
        return new HouseholdInviteId(UUID.randomUUID());
    }
}
