package be.xplore.cookbook.jpa.repository.recipe.entity;

import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.recipe.RecipeIngredient;
import be.xplore.cookbook.jpa.repository.ingredient.entity.JpaIngredientEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "recipe_ingredients")
public class JpaRecipeIngredientEntity {

    @EmbeddedId
    private JpaRecipeIngredientId id;

    @Column(nullable = false)
    private double baseQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Unit unit;

    @ManyToOne()
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id", nullable = false)
    private JpaRecipeEntity recipe;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id", nullable = false)
    private JpaIngredientEntity ingredient;

    protected JpaRecipeIngredientEntity() {
    }

    public JpaRecipeIngredientEntity(
            JpaRecipeEntity recipe, RecipeIngredient recipeIngredient, JpaIngredientEntity ingredient) {
        this.id = new JpaRecipeIngredientId(
                recipe.getId(),
                ingredient.getId()
        );
        this.recipe = recipe;
        this.ingredient = ingredient;
        this.baseQuantity = recipeIngredient.baseQuantity();
        this.unit = recipeIngredient.unit();
    }

    public RecipeIngredient toDomain() {
        return new RecipeIngredient(
                ingredient.toDomainWithoutCategoriesAndUser(),
                baseQuantity,
                unit
        );
    }

    public void setRecipe(JpaRecipeEntity recipe) {
        this.recipe = recipe;
    }
}
