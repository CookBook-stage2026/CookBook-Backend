package be.xplore.cookbook.jpa.repository.householdinvite;

import be.xplore.cookbook.jpa.repository.householdinvite.entity.JpaHouseholdInviteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaHouseholdInviteRepository extends JpaRepository<JpaHouseholdInviteEntity, UUID> {
    Optional<JpaHouseholdInviteEntity> findByTokenHash(String tokenHash);
    List<JpaHouseholdInviteEntity> findAllByHouseholdId(UUID householdId);
}
