package cookbook.stage.backend.repository.jpa.user;

import cookbook.stage.backend.domain.recipe.Recipe;
import cookbook.stage.backend.domain.user.WeekSchedule;
import cookbook.stage.backend.domain.user.WeekScheduleId;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "week_schedules")
public class JpaWeekScheduleEntity {

    @Id
    private UUID id;
    private UUID userId;

    @OneToMany(
            mappedBy = "weekScheduleId",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<JpaDayScheduleEntity> daySchedules = new ArrayList<>();

    protected JpaWeekScheduleEntity() {
    }

    public WeekSchedule toDomain(Map<DayOfWeek, Recipe> resolvedRecipes) {
        return new WeekSchedule(new WeekScheduleId(id), resolvedRecipes);
    }

    public static JpaWeekScheduleEntity fromDomain(WeekSchedule schedule, UUID userId) {
        JpaWeekScheduleEntity entity = new JpaWeekScheduleEntity();
        entity.id = schedule.id().id();
        entity.userId = userId;
        entity.daySchedules = schedule.dailyRecipes().entrySet().stream()
                .map(entry -> JpaDayScheduleEntity.fromDomain(
                        entity.id,
                        entry.getKey(),
                        entry.getValue()
                ))
                .toList();
        return entity;
    }

    public WeekSchedule toDomain() {
        Map<DayOfWeek, Recipe> resolvedRecipes = new EnumMap<>(DayOfWeek.class);
        daySchedules.forEach(d -> resolvedRecipes.put(d.getDayOfWeek(), d.getRecipe().toDomain()));
        return new WeekSchedule(new WeekScheduleId(id), resolvedRecipes);
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public List<JpaDayScheduleEntity> getDaySchedules() {
        return daySchedules;
    }
}
