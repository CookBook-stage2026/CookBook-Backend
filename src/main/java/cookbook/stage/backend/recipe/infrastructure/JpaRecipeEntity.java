package cookbook.stage.backend.recipe.infrastructure;

import jakarta.persistence.*;

import java.util.List;
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "recipe_id")
    private List<JpaRecipeIngredientEntity> ingredients;

    public JpaRecipeEntity(UUID id, String name, String description, int durationInMinutes, List<String> steps, List<JpaRecipeIngredientEntity> ingredients) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.durationInMinutes = durationInMinutes;
        this.steps = steps;
        this.ingredients = ingredients;
    }

    protected JpaRecipeEntity() {
    } // for JPA

    public static JpaRecipeEntity fromDomain(Recipe recipe) {
        // Convert the domain ingredients into JPA entities
        List<JpaRecipeIngredientEntity> jpaIngredients = recipe.getIngredients().stream()
                .map(JpaRecipeIngredientEntity::fromDomain)
                .collect(Collectors.toList());

        // Assuming RecipeId has an id() method that returns a UUID, just like IngredientId
        return new JpaRecipeEntity(
                recipe.getId().id(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getDurationInMinutes(),
                recipe.getSteps(),
                jpaIngredients
        );
    }

    public Recipe toDomain() {
        // Convert the JPA entities back into domain ingredients
        List<RecipeIngredient> domainIngredients = this.ingredients.stream()
                .map(JpaRecipeIngredientEntity::toDomain)
                .collect(Collectors.toList());

        return new Recipe(
                new RecipeId(this.id),
                this.name,
                this.description,
                this.durationInMinutes,
                this.steps,
                domainIngredients
        );
    }
}
