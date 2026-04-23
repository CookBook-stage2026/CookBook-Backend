package cookbook.stage.backend.repository.jpa.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaDayScheduleRepository extends JpaRepository<JpaDayScheduleEntity, UUID> {
    List<JpaDayScheduleEntity> findByWeekScheduleId(UUID weekScheduleId);
}
