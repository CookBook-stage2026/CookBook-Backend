package cookbook.stage.backend.repository.jpa.schedule;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaWeekScheduleRepository extends CrudRepository<JpaWeekScheduleEntity, UUID> {
    Optional<JpaWeekScheduleEntity> findWeekScheduleByUserId(UUID userId);
}
