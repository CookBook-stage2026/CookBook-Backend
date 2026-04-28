package be.xplore.cookbook.jpa.repository.ingredient.entity;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.ingredient.Unit;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ingredients")
public class JpaIngredientEntity {
    @Id
    @Column(name = "ingredient_id")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Unit unit;

    @ElementCollection(targetClass = Category.class)
    @CollectionTable(
            name = "ingredient_categories",
            joinColumns = @JoinColumn(name = "ingredient_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private List<Category> categories = new ArrayList<>();

    public JpaIngredientEntity(UUID id, String name, Unit unit, List<Category> categories) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.categories = categories;
    }

    protected JpaIngredientEntity() {
    }

    public static JpaIngredientEntity fromDomain(Ingredient ingredient) {
        return new JpaIngredientEntity(
                ingredient.id().id(),
                ingredient.name(),
                ingredient.unit(),
                ingredient.categories()
        );
    }

    public Ingredient toDomain() {
        return new Ingredient(
                new IngredientId(id),
                name,
                unit,
                categories
        );
    }

    public Ingredient toDomainWithoutCategories() {
        return new Ingredient(
                new IngredientId(id),
                name,
                unit,
                List.of()
        );
    }

    public UUID getId() {
        return id;
    }
}
