package cookbook.stage.backend.repository.jpa.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaWeekScheduleRepository extends JpaRepository<JpaWeekScheduleEntity, UUID> {
    List<JpaWeekScheduleEntity> findByUserId(UUID userId);
}
