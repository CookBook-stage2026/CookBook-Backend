package be.xplore.cookbook.jpa.repository.recipe.entity;

import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeDetails;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.recipe.RecipeIngredient;
import be.xplore.cookbook.core.domain.recipe.RecipeSummary;
import be.xplore.cookbook.jpa.repository.ingredient.entity.JpaIngredientEntity;
import be.xplore.cookbook.jpa.repository.user.entity.JpaUserEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private Set<JpaRecipeIngredientEntity> ingredients = new HashSet<>();

    @ManyToOne
    private JpaUserEntity user;

    protected JpaRecipeEntity() {
    }

    public JpaRecipeEntity(UUID id, String name, String description, int durationInMinutes,
                           List<String> steps, int servings, JpaUserEntity user) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.durationInMinutes = durationInMinutes;
        this.steps = steps;
        this.servings = servings;
        this.user = user;
    }

    public static JpaRecipeEntity fromDomain(Recipe recipe) {
        JpaRecipeEntity entity = new JpaRecipeEntity(
                recipe.getId().id(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getDurationInMinutes(),
                recipe.getSteps(),
                recipe.getServings(),
                JpaUserEntity.fromDomain(recipe.getUser())
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
                new RecipeDetails(
                        name,
                        description,
                        durationInMinutes,
                        servings,
                        steps
                ),
                domainIngredients,
                user.toDomain()
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
        JpaIngredientEntity jpaIngredient = JpaIngredientEntity.fromDomain(recipeIngredient.ingredient());

        JpaRecipeIngredientEntity entity = new JpaRecipeIngredientEntity(this, recipeIngredient, jpaIngredient);
        ingredients.add(entity);
    }

    public UUID getId() {
        return id;
    }
}
