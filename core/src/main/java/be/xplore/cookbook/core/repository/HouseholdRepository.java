package be.xplore.cookbook.core.repository;

import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.core.domain.household.HouseholdId;
import be.xplore.cookbook.core.domain.user.UserId;

import java.util.List;
import java.util.Optional;

public interface HouseholdRepository {
    Household save(Household houseHold);

    Optional<Household> findById(HouseholdId id);

    List<Household> findAllByUserId(UserId userId);

    boolean removeMember(HouseholdId householdId, UserId userId);

    void deleteById(HouseholdId householdId);

    boolean isMemberOrCreator(HouseholdId householdId, UserId userId);
}
