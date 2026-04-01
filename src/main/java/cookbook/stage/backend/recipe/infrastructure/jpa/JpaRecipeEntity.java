package cookbook.stage.backend.recipe.infrastructure.jpa;

import cookbook.stage.backend.recipe.domain.IngredientAmount;
import cookbook.stage.backend.recipe.domain.Recipe;
import cookbook.stage.backend.recipe.shared.RecipeId;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
    @Column(name = "step_description")
    private List<String> steps;

    @ElementCollection
    @CollectionTable(
            name = "recipe_ingredients",
            joinColumns = @JoinColumn(name = "recipe_id")
    )
    @MapKeyColumn(name = "ingredient_name")
    private Map<String, JpaIngredientAmount> ingredients = new HashMap<>();

    public JpaRecipeEntity(UUID id, String name, String description, int durationInMinutes,
                           List<String> steps, Map<String, JpaIngredientAmount> ingredients) {
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
        var ingredients = recipe.getIngredients().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new JpaIngredientAmount(e.getValue().quantity(), e.getValue().unit())
                ));

        return new JpaRecipeEntity(
                recipe.getId().id(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getDurationInMinutes(),
                recipe.getSteps(),
                ingredients
        );
    }

    public Recipe toDomain() {
        Map<String, IngredientAmount> domainIngredients = ingredients.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new IngredientAmount(e.getValue().quantity(), e.getValue().unit())
                ));

        return new Recipe(
                new RecipeId(id),
                name,
                description,
                durationInMinutes,
                steps,
                domainIngredients
        );
    }
}
