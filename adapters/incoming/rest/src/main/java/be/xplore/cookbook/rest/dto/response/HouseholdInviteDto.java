package be.xplore.cookbook.rest.dto.response;

import java.util.UUID;

public record HouseholdInviteDto(
        UUID householdInviteId,
        boolean revoked
) {
}
