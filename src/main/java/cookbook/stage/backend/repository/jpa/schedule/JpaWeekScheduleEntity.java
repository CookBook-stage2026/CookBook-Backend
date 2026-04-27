package cookbook.stage.backend.repository.jpa.schedule;

import cookbook.stage.backend.domain.user.User;
import cookbook.stage.backend.domain.weekschedule.WeekSchedule;
import cookbook.stage.backend.domain.weekschedule.WeekScheduleId;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "week_schedules")
public class JpaWeekScheduleEntity {

    @Id
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @OneToMany(
            mappedBy = "weekSchedule",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<JpaDayScheduleEntity> daySchedules = new ArrayList<>();

    protected JpaWeekScheduleEntity() {
    }

    public JpaWeekScheduleEntity(UUID id, UUID userId, List<JpaDayScheduleEntity> daySchedules) {
        this.id = id;
        this.userId = userId;
        this.daySchedules = daySchedules;
    }

    public WeekSchedule toDomain(User user) {
        return new WeekSchedule(new WeekScheduleId(id), user, daySchedules.stream()
                .map(JpaDayScheduleEntity::toDomain).toList());
    }

    public static JpaWeekScheduleEntity fromDomain(WeekSchedule schedule) {
        JpaWeekScheduleEntity entity = new JpaWeekScheduleEntity(
                schedule.id().id(),
                schedule.user().getId().id(),
                new ArrayList<>()
        );

        schedule.dailyRecipes().forEach(day -> {
            JpaDayScheduleEntity dayEntity = JpaDayScheduleEntity.fromDomain(day, entity);
            entity.daySchedules.add(dayEntity);
        });

        return entity;
    }

    public UUID getId() {
        return id;
    }
}
