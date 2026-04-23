package cookbook.stage.backend.repository.jpa.user;

import cookbook.stage.backend.domain.recipe.Recipe;
import cookbook.stage.backend.domain.user.WeekSchedule;
import cookbook.stage.backend.domain.user.WeekScheduleId;
import cookbook.stage.backend.repository.jpa.recipe.JpaRecipeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
public class JpaWeekScheduleEntity {

    @Id
    private UUID id;
    private UUID userId;
    private int year;
    private int weekNumber;

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
        return new WeekSchedule(new WeekScheduleId(id), year, weekNumber, resolvedRecipes);
    }

    public static JpaWeekScheduleEntity fromDomain(WeekSchedule schedule, UUID userId) {
        JpaWeekScheduleEntity entity = new JpaWeekScheduleEntity();
        entity.id = schedule.id().id();
        entity.userId = userId;
        entity.year = schedule.year();
        entity.weekNumber = schedule.weekNumber();
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
        return new WeekSchedule(new WeekScheduleId(id), year, weekNumber, resolvedRecipes);
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public int getYear() {
        return year;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public List<JpaDayScheduleEntity> getDaySchedules() {
        return daySchedules;
    }
}
