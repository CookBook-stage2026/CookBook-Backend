package cookbook.stage.backend.repository.jpa.schedule;

import cookbook.stage.backend.domain.week_schedule.WeekSchedule;
import cookbook.stage.backend.domain.week_schedule.WeekScheduleId;
import cookbook.stage.backend.repository.jpa.user.JpaUserEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "week_schedules")
public class JpaWeekScheduleEntity {

    @Id
    private UUID id;

    @OneToOne
    private JpaUserEntity user;

    @OneToMany(
            mappedBy = "weekSchedule",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<JpaDayScheduleEntity> daySchedules = new ArrayList<>();

    protected JpaWeekScheduleEntity() {
    }

    public JpaWeekScheduleEntity(UUID id, JpaUserEntity user, List<JpaDayScheduleEntity> daySchedules) {
        this.id = id;
        this.user = user;
        this.daySchedules = daySchedules;
    }

    public WeekSchedule toDomain() {
        return new WeekSchedule(new WeekScheduleId(id), user.toDomain(), daySchedules.stream()
                .map(JpaDayScheduleEntity::toDomain).toList());
    }

    public static JpaWeekScheduleEntity fromDomain(WeekSchedule schedule) {
        JpaWeekScheduleEntity entity = new JpaWeekScheduleEntity(
                schedule.id().id(),
                JpaUserEntity.fromDomain(schedule.user()),
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

    public JpaUserEntity getUser() {
        return user;
    }
}
