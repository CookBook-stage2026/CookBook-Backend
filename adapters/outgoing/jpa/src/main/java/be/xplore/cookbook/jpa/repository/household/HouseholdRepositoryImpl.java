package be.xplore.cookbook.jpa.repository.household;

import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.core.domain.household.HouseholdId;
import be.xplore.cookbook.core.domain.household.exception.HouseholdNotFoundException;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.repository.HouseholdRepository;
import be.xplore.cookbook.jpa.repository.household.entity.JpaHouseholdEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Override
    public List<Household> findAllByUserId(UserId userId) {
        return jpaHouseholdRepository.findAllHouseholdsByUserId(userId.id())
                .stream().map(JpaHouseholdEntity::toDomain).toList();
    }

    @Override
    public void removeMember(HouseholdId householdId, UserId userId) {
        JpaHouseholdEntity household = jpaHouseholdRepository.findById(householdId.id())
                .orElseThrow(() -> new HouseholdNotFoundException(householdId));

        household.getMembers().removeIf(member ->
                member.getId().equals(userId.id())
        );

        jpaHouseholdRepository.save(household);
    }

    @Override
    public void deleteById(HouseholdId id) {
        jpaHouseholdRepository.deleteById(id.id());
    }
}
