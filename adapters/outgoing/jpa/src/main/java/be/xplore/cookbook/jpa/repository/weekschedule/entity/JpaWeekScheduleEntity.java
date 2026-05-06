package be.xplore.cookbook.jpa.repository.weekschedule.entity;

import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;
import be.xplore.cookbook.core.domain.weekschedule.WeekScheduleId;
import be.xplore.cookbook.jpa.repository.user.entity.JpaUserEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "week_schedules")
public class JpaWeekScheduleEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private JpaUserEntity user;

    @OneToMany(
            mappedBy = "weekSchedule",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<JpaDayScheduleEntity> daySchedules = new ArrayList<>();

    private LocalDate weekStartDate;

    protected JpaWeekScheduleEntity() {
    }

    public JpaWeekScheduleEntity(UUID id, JpaUserEntity user, LocalDate weekStartDate,
                                 List<JpaDayScheduleEntity> daySchedules) {
        this.id = id;
        this.daySchedules = daySchedules;
        this.user = user;
        this.weekStartDate = weekStartDate;
    }

    public WeekSchedule toDomain() {
        return new WeekSchedule(new WeekScheduleId(id), user.toDomain(), weekStartDate,
                daySchedules.stream()
                .map(JpaDayScheduleEntity::toDomain).toList());
    }

    public static JpaWeekScheduleEntity fromDomain(WeekSchedule schedule) {
        JpaWeekScheduleEntity entity = new JpaWeekScheduleEntity(
                schedule.id().id(),
                JpaUserEntity.fromDomain(schedule.user()),
                schedule.weekStartDate(),
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
