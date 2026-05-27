package be.xplore.cookbook.jpa.repository.weekschedule;

import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwnerType;
import be.xplore.cookbook.jpa.repository.weekschedule.entity.JpaWeekScheduleEntity;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying(clearAutomatically = true)
    @Query("""
                DELETE FROM JpaDayScheduleEntity d
                WHERE d.recipe.user.id = :userId
                AND d.weekSchedule.owner.ownerId = :ownerId
            """)
    void deleteAllByOwnerAndRecipeUser(
            @Param("ownerId") UUID ownerId,
            @Param("userId") UUID userId
    );

    @Modifying(clearAutomatically = true)
    @Query("""
                DELETE FROM JpaDayScheduleEntity d
                WHERE d.recipe.id = :recipeId
                AND d.weekSchedule.owner.ownerType = :ownerType
            """)
    void deleteByRecipeIdAndOwnerType(
            @Param("recipeId") UUID recipeId,
            @Param("ownerType") ScheduleOwnerType ownerType
    );
}
