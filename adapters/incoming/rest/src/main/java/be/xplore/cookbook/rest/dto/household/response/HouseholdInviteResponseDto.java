package be.xplore.cookbook.rest.dto.household.response;

import be.xplore.cookbook.core.domain.householdinvite.HouseholdInvite;

import java.time.Instant;
import java.util.UUID;

public record HouseholdInviteResponseDto(UUID id, Instant expiresAt, Instant createdOn, boolean revoked) {
    public static HouseholdInviteResponseDto fromDomain(HouseholdInvite invite) {
        return new HouseholdInviteResponseDto(
                invite.id().id(),
                invite.expiresAt(),
                invite.createdOn(),
                invite.revoked()
        );
    }
}
