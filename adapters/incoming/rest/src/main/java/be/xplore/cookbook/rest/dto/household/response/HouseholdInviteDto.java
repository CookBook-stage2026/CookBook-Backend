package be.xplore.cookbook.rest.dto.household.response;

import java.util.UUID;

public record HouseholdInviteDto(
        UUID householdInviteId,
        boolean revoked
) {
}
