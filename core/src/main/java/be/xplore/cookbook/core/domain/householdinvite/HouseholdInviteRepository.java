package be.xplore.cookbook.core.domain.householdinvite;

import java.util.Optional;

public interface HouseholdInviteRepository {
    HouseholdInvite save(HouseholdInvite invite);
    Optional<HouseholdInvite> findByTokenHash(String tokenHash);
    Optional<HouseholdInvite> findById(HouseholdInviteId id);
}
