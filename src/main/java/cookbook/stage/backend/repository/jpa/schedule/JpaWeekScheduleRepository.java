package cookbook.stage.backend.repository.jpa.schedule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaWeekScheduleRepository extends JpaRepository<JpaWeekScheduleEntity, UUID> {
    List<JpaWeekScheduleEntity> findWeekScheduleByUser_Id(UUID userId);

    void deleteByUser_Id(UUID userId);
}
