package be.xplore.cookbook.jpa.repository.household;

import be.xplore.cookbook.jpa.repository.household.entity.JpaHouseholdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface JpaHouseholdRepository extends JpaRepository<JpaHouseholdEntity, UUID> {

    @Query("""
            SELECT DISTINCT h FROM JpaHouseholdEntity h
                        LEFT JOIN h.members u
                        LEFT JOIN h.creator c
                        WHERE u.id = :userId OR c.id = :userId
            """)
    List<JpaHouseholdEntity> findAllHouseholdsByUserId(UUID userId);
}
