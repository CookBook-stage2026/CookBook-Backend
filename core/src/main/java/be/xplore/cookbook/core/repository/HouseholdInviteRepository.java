package be.xplore.cookbook.core.repository;

import be.xplore.cookbook.core.domain.householdinvite.HouseholdInvite;
import be.xplore.cookbook.core.domain.householdinvite.HouseholdInviteId;

import java.util.Optional;

public interface HouseholdInviteRepository {
    HouseholdInvite save(HouseholdInvite invite);
    Optional<HouseholdInvite> findByTokenHash(String tokenHash);
    Optional<HouseholdInvite> findById(HouseholdInviteId id);
}
