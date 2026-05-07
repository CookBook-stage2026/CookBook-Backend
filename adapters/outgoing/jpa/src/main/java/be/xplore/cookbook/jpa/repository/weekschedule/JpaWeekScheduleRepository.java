package be.xplore.cookbook.jpa.repository.weekschedule;

import be.xplore.cookbook.jpa.repository.weekschedule.entity.JpaWeekScheduleEntity;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaWeekScheduleRepository extends CrudRepository<JpaWeekScheduleEntity, UUID> {
    List<JpaWeekScheduleEntity> findByUserIdOrderByWeekStartDateDesc(UUID userId);

    List<JpaWeekScheduleEntity> findByUserIdAndWeekStartDateBetweenOrderByWeekStartDateDesc(
            UUID userId, LocalDate startFrom, LocalDate startTo);

    Optional<JpaWeekScheduleEntity> findByUserIdAndWeekStartDate(UUID userId, LocalDate weekStartDate);
}
