package be.xplore.cookbook.core.domain.household;

import java.util.Objects;
import java.util.UUID;

public record HouseholdId(UUID id) {
    public HouseholdId {
        Objects.requireNonNull(id, "User id cannot be null!");
    }

    public static HouseholdId create() {
        return new HouseholdId(UUID.randomUUID());
    }
}
