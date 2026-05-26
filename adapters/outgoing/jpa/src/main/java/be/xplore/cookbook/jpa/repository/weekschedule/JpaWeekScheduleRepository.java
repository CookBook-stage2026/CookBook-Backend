package be.xplore.cookbook.jpa.repository.weekschedule;

import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwnerType;
import be.xplore.cookbook.jpa.repository.weekschedule.entity.JpaWeekScheduleEntity;
import org.springframework.data.repository.CrudRepository;

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
}
