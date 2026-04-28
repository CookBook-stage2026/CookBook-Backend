package be.xplore.cookbook.jpa.repository.schedule;

import be.xplore.cookbook.jpa.repository.schedule.entity.JpaWeekScheduleEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaWeekScheduleRepository extends CrudRepository<JpaWeekScheduleEntity, UUID> {
    Optional<JpaWeekScheduleEntity> findWeekScheduleByUserId(UUID userId);
}
