package be.xplore.cookbook.core.domain.householdinvite;

import be.xplore.cookbook.core.domain.household.HouseholdId;
import be.xplore.cookbook.core.domain.user.UserId;

import java.time.Duration;
import java.time.Instant;

public record HouseholdInvite(
        HouseholdInviteId id,
        HouseholdId householdId,
        String tokenHash,
        Instant expiresAt,
        Instant createdOn,
        boolean revoked,
        UserId createdBy
) {
    public HouseholdInvite(HouseholdId householdId, String tokenHash, Duration duration, UserId createdBy) {
        this(HouseholdInviteId.create(),
                householdId,
                tokenHash,
                Instant.now().plus(duration),
                Instant.now(),
                false,
                createdBy);
    }

    public boolean isValid() {
        return !revoked && Instant.now().isBefore(expiresAt);
    }

    public HouseholdInvite revoke() {
        return new HouseholdInvite(id, householdId, tokenHash, expiresAt, createdOn, true, createdBy);
    }
}
