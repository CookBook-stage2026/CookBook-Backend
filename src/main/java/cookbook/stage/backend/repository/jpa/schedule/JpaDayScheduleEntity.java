package cookbook.stage.backend.repository.jpa.schedule;

import cookbook.stage.backend.domain.week_schedule.DaySchedule;
import cookbook.stage.backend.domain.week_schedule.DayScheduleId;
import cookbook.stage.backend.repository.jpa.recipe.JpaRecipeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.DayOfWeek;
import java.util.UUID;

@Entity
@Table(name = "day_schedules",
        uniqueConstraints = @UniqueConstraint(columnNames = {"week_schedule_id", "day_of_week"}))
public class JpaDayScheduleEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "week_schedule_id")
    private JpaWeekScheduleEntity weekSchedule;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private JpaRecipeEntity recipe;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    protected JpaDayScheduleEntity() {
    }

    public JpaDayScheduleEntity(UUID id, JpaWeekScheduleEntity weekSchedule,
                                JpaRecipeEntity recipe, DayOfWeek dayOfWeek) {
        this.id = id;
        this.weekSchedule = weekSchedule;
        this.recipe = recipe;
        this.dayOfWeek = dayOfWeek;
    }

    public static JpaDayScheduleEntity fromDomain(DaySchedule daySchedule, JpaWeekScheduleEntity weekSchedule) {
        return new JpaDayScheduleEntity(
                daySchedule.id().id(),
                weekSchedule,
                JpaRecipeEntity.fromDomain(daySchedule.recipe()),
                daySchedule.day()
        );
    }

    public DaySchedule toDomain() {
        return new DaySchedule(new DayScheduleId(id), recipe.toDomain(), dayOfWeek);
    }

    public UUID getId() {
        return id;
    }

    public JpaRecipeEntity getRecipe() {
        return recipe;
    }
}
