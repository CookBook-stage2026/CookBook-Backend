package cookbook.stage.backend.repository.jpa.schedule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaDayScheduleRepository extends JpaRepository<JpaDayScheduleEntity, UUID> {
}
