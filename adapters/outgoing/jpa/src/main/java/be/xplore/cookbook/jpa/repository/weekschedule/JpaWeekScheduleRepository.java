package be.xplore.cookbook.jpa.repository.weekschedule;

import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwnerType;
import be.xplore.cookbook.jpa.repository.weekschedule.entity.JpaWeekScheduleEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaWeekScheduleRepository extends CrudRepository<JpaWeekScheduleEntity, UUID> {
    List<JpaWeekScheduleEntity>
    findByOwnerOwnerIdAndOwnerOwnerTypeOrderByWeekStartDateDesc(
            UUID ownerId,
            ScheduleOwnerType ownerType
    );

    List<JpaWeekScheduleEntity>
    findByOwnerOwnerIdAndWeekStartDateBetweenOrderByWeekStartDateDesc(
            UUID ownerId,
            LocalDate startFrom,
            LocalDate startTo
    );

    Optional<JpaWeekScheduleEntity>
    findByOwnerOwnerIdAndOwnerOwnerTypeAndWeekStartDate(
            UUID ownerId,
            ScheduleOwnerType ownerType,
            LocalDate weekStartDate
    );

    @EntityGraph(attributePaths = {
            "daySchedules",
            "daySchedules.recipe"
    })
    @Query("""
                SELECT DISTINCT ws FROM JpaWeekScheduleEntity ws
                JOIN ws.daySchedules d
                WHERE d.recipe.id = :recipeId
                AND ws.owner.ownerType = :ownerType
            """)
    List<JpaWeekScheduleEntity> findAllByRecipeAndOwnerTypeWithDays(
            @Param("recipeId") UUID recipeId,
            @Param("ownerType") ScheduleOwnerType ownerType
    );

    @EntityGraph(attributePaths = {
            "daySchedules",
            "owner"
    })
    @Query("""
                SELECT DISTINCT ws FROM JpaWeekScheduleEntity ws
                JOIN ws.daySchedules d
                WHERE d.recipe.user.id = :userId
                AND ws.owner.ownerId = :householdId
            """)
    List<JpaWeekScheduleEntity> findAllByUserAndOwnerTypeWithDays(
            @Param("userId") UUID userId,
            @Param("householdId") UUID householdId
    );
}
