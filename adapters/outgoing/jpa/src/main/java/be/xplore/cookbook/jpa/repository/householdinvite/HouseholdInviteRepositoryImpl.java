package be.xplore.cookbook.jpa.repository.householdinvite;

import be.xplore.cookbook.core.domain.householdinvite.HouseholdInvite;
import be.xplore.cookbook.core.domain.householdinvite.HouseholdInviteId;
import be.xplore.cookbook.core.repository.HouseholdInviteRepository;
import be.xplore.cookbook.jpa.repository.household.entity.JpaHouseholdEntity;
import be.xplore.cookbook.jpa.repository.householdinvite.entity.JpaHouseholdInviteEntity;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class HouseholdInviteRepositoryImpl implements HouseholdInviteRepository {
    private final JpaHouseholdInviteRepository jpaHouseholdInviteRepository;
    private final EntityManager entityManager;

    public HouseholdInviteRepositoryImpl(JpaHouseholdInviteRepository jpaHouseholdInviteRepository,
                                         EntityManager entityManager) {
        this.jpaHouseholdInviteRepository = jpaHouseholdInviteRepository;
        this.entityManager = entityManager;
    }

    @Override
    public HouseholdInvite save(HouseholdInvite invite) {
        // Only gets ID
        JpaHouseholdEntity householdRef = entityManager.getReference(JpaHouseholdEntity.class,
                invite.householdId().id());
        return jpaHouseholdInviteRepository.save(JpaHouseholdInviteEntity.fromDomain(invite, householdRef)).toDomain();
    }

    @Override
    public Optional<HouseholdInvite> findByTokenHash(String tokenHash) {
        return jpaHouseholdInviteRepository.findByTokenHash(tokenHash)
                .map(JpaHouseholdInviteEntity::toDomain);
    }

    @Override
    public Optional<HouseholdInvite> findById(HouseholdInviteId id) {
        return jpaHouseholdInviteRepository.findById(id.id())
                .map(JpaHouseholdInviteEntity::toDomain);
    }
}
