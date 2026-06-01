package be.xplore.cookbook.jpa.repository.weekschedule.entity;

import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwner;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwnerType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.UUID;

@Embeddable
public class JpaScheduleOwner {

    private UUID ownerId;

    @Enumerated(EnumType.STRING)
    private ScheduleOwnerType ownerType;

    protected JpaScheduleOwner() {
    }

    public JpaScheduleOwner(UUID ownerId, ScheduleOwnerType ownerType) {
        this.ownerId = ownerId;
        this.ownerType = ownerType;
    }

    public ScheduleOwner toDomain() {
        return new ScheduleOwner(ownerId, ownerType);
    }

    public static JpaScheduleOwner fromDomain(ScheduleOwner owner) {
        return new JpaScheduleOwner(owner.ownerId(), owner.ownerType());
    }
}
