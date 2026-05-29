package be.xplore.cookbook.rest.dto.household.response;


import be.xplore.cookbook.core.domain.householdinvite.HouseholdInviteToken;

import java.time.Instant;
import java.util.UUID;

public record HouseholdInviteTokenResponseDto(UUID id, String token, Instant expiresAt) {
    public static HouseholdInviteTokenResponseDto fromDomain(HouseholdInviteToken inviteToken) {
        return new HouseholdInviteTokenResponseDto(
                inviteToken.invite().id().id(),
                inviteToken.plainToken(),
                inviteToken.invite().expiresAt()
        );
    }
}
