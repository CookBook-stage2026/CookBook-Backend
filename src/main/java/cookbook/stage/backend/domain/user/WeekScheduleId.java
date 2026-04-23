package cookbook.stage.backend.domain.user;

import cookbook.stage.backend.domain.exception.NotFoundException;

import java.util.Objects;
import java.util.UUID;

public record WeekScheduleId(UUID id) {
    public WeekScheduleId {
        Objects.requireNonNull(id, "Week schedule id cannot be null!");
    }

    public NotFoundException notFound() {
        return new NotFoundException("Week schedule [" + id + "] not found");
    }

    public static WeekScheduleId create() {
        return new WeekScheduleId(UUID.randomUUID());
    }
}
