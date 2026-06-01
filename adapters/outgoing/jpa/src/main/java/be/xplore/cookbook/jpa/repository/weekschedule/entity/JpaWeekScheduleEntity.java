package be.xplore.cookbook.jpa.repository.weekschedule.entity;

import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;
import be.xplore.cookbook.core.domain.weekschedule.WeekScheduleId;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
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

    @Embedded
    private JpaScheduleOwner owner;

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

    public WeekSchedule toDomain() {
        return new WeekSchedule(
                new WeekScheduleId(id),
                owner.toDomain(),
                weekStartDate,
                daySchedules.stream()
                        .map(JpaDayScheduleEntity::toDomain)
                        .toList()
        );
    }

    public static JpaWeekScheduleEntity fromDomain(WeekSchedule schedule) {
        JpaWeekScheduleEntity entity = new JpaWeekScheduleEntity();

        entity.id = schedule.id().id();
        entity.owner = JpaScheduleOwner.fromDomain(schedule.owner());
        entity.weekStartDate = schedule.weekStartDate();

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
