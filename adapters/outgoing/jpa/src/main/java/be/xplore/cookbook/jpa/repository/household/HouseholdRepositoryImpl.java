package be.xplore.cookbook.jpa.repository.household;

import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.core.domain.household.HouseholdId;
import be.xplore.cookbook.core.domain.household.HouseholdRepository;
import be.xplore.cookbook.jpa.repository.household.entity.JpaHouseholdEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class HouseholdRepositoryImpl implements HouseholdRepository {
    private final JpaHouseholdRepository jpaHouseholdRepository;

    public HouseholdRepositoryImpl(JpaHouseholdRepository jpaHouseholdRepository) {
        this.jpaHouseholdRepository = jpaHouseholdRepository;
    }

    @Override
    public Household save(Household houseHold) {
        return jpaHouseholdRepository.save(JpaHouseholdEntity.fromDomain(houseHold)).toDomain();
    }

    @Override
    public Optional<Household> findById(HouseholdId id) {
        return jpaHouseholdRepository.findById(id.id()).map(JpaHouseholdEntity::toDomain);
    }
}
