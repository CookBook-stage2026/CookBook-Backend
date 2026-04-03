package cookbook.stage.backend.recipe.infrastructure.jpa;

import cookbook.stage.backend.recipe.domain.Recipe;
import cookbook.stage.backend.recipe.domain.RecipeIngredient;
import cookbook.stage.backend.recipe.domain.RecipeSummary;
import cookbook.stage.backend.recipe.shared.RecipeId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "recipes")
public class JpaRecipeEntity {

    @Id
    @Column(name = "recipe_id")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int durationInMinutes;

    @Column(nullable = false)
    private int servings;

    @ElementCollection
    @CollectionTable(
            name = "recipe_steps",
            joinColumns = @JoinColumn(name = "recipe_id")
    )
    @OrderColumn(name = "step_order")
    private List<String> steps;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JpaRecipeIngredientEntity> ingredients = new ArrayList<>();

    protected JpaRecipeEntity() {
    }

    public JpaRecipeEntity(UUID id, String name, String description, int durationInMinutes,
                           List<String> steps, int servings) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.durationInMinutes = durationInMinutes;
        this.steps = steps;
        this.servings = servings;
    }

    public static JpaRecipeEntity fromDomain(Recipe recipe) {
        JpaRecipeEntity entity = new JpaRecipeEntity(
                recipe.getId().id(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getDurationInMinutes(),
                recipe.getSteps(),
                recipe.getServings()
        );
        recipe.getIngredients().forEach(entity::addIngredient);
        return entity;
    }

    public Recipe toDomain() {
        List<RecipeIngredient> domainIngredients = ingredients.stream()
                .map(JpaRecipeIngredientEntity::toDomain)
                .toList();

        return new Recipe(
                new RecipeId(id),
                name,
                description,
                durationInMinutes,
                steps,
                domainIngredients,
                servings
        );
    }

    public RecipeSummary toSummary() {
        return new RecipeSummary(
                new RecipeId(id),
                name,
                description,
                durationInMinutes
        );
    }

    public void addIngredient(RecipeIngredient recipeIngredient) {
        JpaRecipeIngredientEntity entity = new JpaRecipeIngredientEntity(this, recipeIngredient);
        ingredients.add(entity);
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JpaRecipeEntity that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
