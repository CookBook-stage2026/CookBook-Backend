package be.xplore.cookbook.core.domain.household;

import java.util.Optional;

public interface HouseholdRepository {
    Household save(Household houseHold);
    Optional<Household> findById(HouseholdId id);
}
