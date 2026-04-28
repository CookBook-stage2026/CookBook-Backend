package be.xplore.cookbook.core.domain.weekschedule;

import be.xplore.cookbook.core.domain.exception.NotFoundException;

import java.util.Objects;
import java.util.UUID;

public record DayScheduleId(UUID id) {
    public DayScheduleId {
        Objects.requireNonNull(id, "Week schedule id cannot be null!");
    }

    public NotFoundException notFound() {
        return new NotFoundException("Day schedule [" + id + "] not found");
    }

    public static DayScheduleId create() {
        return new DayScheduleId(UUID.randomUUID());
    }
}
