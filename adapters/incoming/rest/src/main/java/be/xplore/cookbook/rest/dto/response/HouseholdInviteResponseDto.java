package be.xplore.cookbook.rest.dto.response;


import be.xplore.cookbook.core.domain.householdinvite.HouseholdInviteToken;

import java.time.Instant;
import java.util.UUID;

public record HouseholdInviteResponseDto(UUID id, String token, Instant expiresAt) {
    public static HouseholdInviteResponseDto fromDomain(HouseholdInviteToken inviteToken) {
        return new HouseholdInviteResponseDto(
                inviteToken.invite().id().id(),
                inviteToken.plainToken(),
                inviteToken.invite().expiresAt()
        );
    }
}
