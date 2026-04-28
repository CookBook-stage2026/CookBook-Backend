package be.xplore.cookbook.core.domain.weekschedule;

import java.util.Objects;
import java.util.UUID;

public record WeekScheduleId(UUID id) {
    public WeekScheduleId {
        Objects.requireNonNull(id, "Week schedule id cannot be null!");
    }
    public static WeekScheduleId create() {
        return new WeekScheduleId(UUID.randomUUID());
    }
}
