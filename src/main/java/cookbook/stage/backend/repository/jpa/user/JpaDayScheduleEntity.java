package cookbook.stage.backend.repository.jpa.user;

import cookbook.stage.backend.domain.recipe.Recipe;
import cookbook.stage.backend.repository.jpa.recipe.JpaRecipeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.DayOfWeek;
import java.util.UUID;

@Entity
@Table(name = "day_schedules")
public class JpaDayScheduleEntity {

    @Id
    private UUID id;

    private UUID weekScheduleId;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private JpaRecipeEntity recipe;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    public JpaRecipeEntity getRecipe() {
        return recipe;
    }

    public void setRecipe(JpaRecipeEntity recipe) {
        this.recipe = recipe;
    }

    protected JpaDayScheduleEntity() {
    }

    public static JpaDayScheduleEntity fromDomain(UUID weekScheduleId, DayOfWeek day, Recipe recipe) {
        JpaDayScheduleEntity entity = new JpaDayScheduleEntity();
        entity.id = UUID.randomUUID();
        entity.weekScheduleId = weekScheduleId;
        entity.dayOfWeek = day;
        entity.recipe = JpaRecipeEntity.fromDomain(recipe);
        return entity;
    }

    public UUID getId() {
        return id;
    }

    public UUID getWeekScheduleId() {
        return weekScheduleId;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }
}
