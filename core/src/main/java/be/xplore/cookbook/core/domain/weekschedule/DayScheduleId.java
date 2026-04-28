package be.xplore.cookbook.core.domain.weekschedule;

import java.util.Objects;
import java.util.UUID;

public record DayScheduleId(UUID id) {
    public DayScheduleId {
        Objects.requireNonNull(id, "Week schedule id cannot be null!");
    }

    public static DayScheduleId create() {
        return new DayScheduleId(UUID.randomUUID());
    }
}
