package be.xplore.cookbook.jpa.repository.household;

import be.xplore.cookbook.jpa.repository.household.entity.JpaHouseholdEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaHouseholdRepository extends JpaRepository<JpaHouseholdEntity, UUID> {
}
