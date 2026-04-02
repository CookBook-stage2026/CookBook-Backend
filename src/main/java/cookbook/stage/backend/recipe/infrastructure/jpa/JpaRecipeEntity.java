package cookbook.stage.backend.recipe.infrastructure.jpa;

import cookbook.stage.backend.recipe.domain.Ingredient;
import cookbook.stage.backend.recipe.domain.Recipe;
import cookbook.stage.backend.recipe.domain.RecipeSummary;
import cookbook.stage.backend.recipe.shared.RecipeId;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
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

    @ElementCollection
    @CollectionTable(
            name = "recipe_steps",
            joinColumns = @JoinColumn(name = "recipe_id")
    )
    @OrderColumn(name = "step_order")
    private List<String> steps;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JpaIngredient> ingredients = new ArrayList<>();

    public JpaRecipeEntity(UUID id, String name, String description, int durationInMinutes,
                           List<String> steps, List<JpaIngredient> ingredients) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.durationInMinutes = durationInMinutes;
        this.steps = steps;
        this.ingredients = ingredients;
    }

    protected JpaRecipeEntity() {
    }

    public static JpaRecipeEntity fromDomain(Recipe recipe) {
        JpaRecipeEntity entity = new JpaRecipeEntity(
                recipe.getId().id(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getDurationInMinutes(),
                recipe.getSteps(),
                recipe.getIngredients().stream()
                        .map(i -> new JpaIngredient(i.name(), i.quantity(), i.unit(), null))
                        .toList()
        );

        entity.ingredients.forEach(i -> i.setRecipe(entity));
        return entity;
    }

    public Recipe toDomain() {
        List<Ingredient> domainIngredients = ingredients.stream()
                .map(i -> new Ingredient(i.getName(), i.getQuantity(), i.getUnit()))
                .toList();

        return new Recipe(
                new RecipeId(id),
                name,
                description,
                durationInMinutes,
                steps,
                domainIngredients
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
}
