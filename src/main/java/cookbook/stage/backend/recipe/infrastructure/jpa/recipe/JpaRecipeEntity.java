package cookbook.stage.backend.recipe.infrastructure.jpa.recipe;

import cookbook.stage.backend.recipe.domain.recipe.Recipe;
import cookbook.stage.backend.recipe.domain.recipe.RecipeId;
import cookbook.stage.backend.recipe.domain.recipe.RecipeIngredient;
import cookbook.stage.backend.recipe.domain.recipe.RecipeSummary;
import cookbook.stage.backend.recipe.infrastructure.jpa.ingredient.JpaIngredientEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
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

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
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
        // This jpaIngredient is only used as reference, it should not be stored in db again
        JpaIngredientEntity jpaIngredient = JpaIngredientEntity.fromDomain(recipeIngredient.ingredient());

        JpaRecipeIngredientEntity entity = new JpaRecipeIngredientEntity(this, recipeIngredient, jpaIngredient);
        ingredients.add(entity);
    }

    public UUID getId() {
        return id;
    }
}
