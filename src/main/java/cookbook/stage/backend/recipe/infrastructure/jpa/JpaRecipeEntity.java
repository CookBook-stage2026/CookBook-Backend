package cookbook.stage.backend.recipe.infrastructure.jpa;

import cookbook.stage.backend.recipe.domain.Recipe;
import cookbook.stage.backend.recipe.domain.RecipeIngredient;
import cookbook.stage.backend.recipe.shared.RecipeId;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

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

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JpaRecipeIngredientEntity> ingredients;

    public JpaRecipeEntity(UUID id, String name, String description, int durationInMinutes, List<String> steps) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.durationInMinutes = durationInMinutes;
        this.steps = steps;
    }

    protected JpaRecipeEntity() {
    }

    public static JpaRecipeEntity fromDomain(Recipe recipe) {
        JpaRecipeEntity jpaRecipe = new JpaRecipeEntity(
                recipe.getId().id(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getDurationInMinutes(),
                recipe.getSteps()
        );

        List<JpaRecipeIngredientEntity> jpaIngredients = recipe.getIngredients().stream()
                .map(ri -> JpaRecipeIngredientEntity.fromDomain(ri, jpaRecipe))
                .toList();

        jpaRecipe.ingredients.addAll(jpaIngredients);
        return jpaRecipe;
    }

    public Recipe toDomain() {
        List<RecipeIngredient> domainIngredients = this.ingredients.stream()
                .map(JpaRecipeIngredientEntity::toDomain)
                .collect(Collectors.toList());

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
