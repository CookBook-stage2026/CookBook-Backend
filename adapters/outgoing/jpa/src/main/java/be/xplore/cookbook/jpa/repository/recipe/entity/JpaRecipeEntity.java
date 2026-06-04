package be.xplore.cookbook.jpa.repository.recipe.entity;

import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeDetails;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.recipe.RecipeIngredient;
import be.xplore.cookbook.core.domain.recipe.RecipeSummary;
import be.xplore.cookbook.jpa.repository.ingredient.entity.JpaIngredientEntity;
import be.xplore.cookbook.jpa.repository.ingredient.entity.JpaMacroEmbeddable;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "recipes")
public class JpaRecipeEntity {
    private static final int MAX_LENGTH_DESCRIPTION = 512;
    private static final int MAX_LENGTH_STEPS = 1024;

    @Id
    @Column(name = "recipe_id")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = MAX_LENGTH_DESCRIPTION)
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
    @Column(name = "step", length = MAX_LENGTH_STEPS, nullable = false)
    @OrderColumn(name = "step_order")
    private List<String> steps;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<JpaRecipeIngredientEntity> ingredients = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "recipe_macros",
            joinColumns = @JoinColumn(name = "recipe_id")
    )
    private List<JpaMacroEmbeddable> macros = new ArrayList<>();

    @Column(nullable = false)
    private boolean isPublic;

    @ManyToOne
    private JpaUserEntity user;

    public JpaRecipeEntity() {
    }

    public static JpaRecipeEntity fromDomain(Recipe recipe) {
        JpaRecipeEntity entity = new JpaRecipeEntity();

        entity.id = recipe.getId().id();
        entity.name = recipe.getName();
        entity.description = recipe.getDescription();
        entity.durationInMinutes = recipe.getDurationInMinutes();
        entity.steps = recipe.getSteps();
        entity.servings = recipe.getServings();
        entity.isPublic = recipe.isPublic();
        entity.user = JpaUserEntity.fromDomain(recipe.getUser());
        entity.macros = recipe.getMacros().stream().map(JpaMacroEmbeddable::fromDomain).toList();

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
                isPublic,
                user.toDomain(),
                macros != null ? macros.stream().map(JpaMacroEmbeddable::toDomain).toList() : List.of()
        );
    }

    public void updateFromDomain(Recipe recipe) {
        this.name = recipe.getName();
        this.description = recipe.getDescription();
        this.durationInMinutes = recipe.getDurationInMinutes();
        this.steps = recipe.getSteps();
        this.servings = recipe.getServings();
        this.isPublic = recipe.isPublic();
        this.macros = recipe.getMacros().stream()
                .map(JpaMacroEmbeddable::fromDomain)
                .toList();

        this.ingredients.clear();
        recipe.getIngredients().forEach(this::addIngredient);
    }

    public RecipeSummary toSummary() {
        return new RecipeSummary(
                new RecipeId(id),
                name,
                description,
                durationInMinutes,
                user.toDomain()
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
