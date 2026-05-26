package be.xplore.cookbook.jpa.repository.ingredient.entity;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.jpa.repository.user.entity.JpaUserEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private JpaUserEntity user;

    public JpaIngredientEntity(UUID id, String name, Unit unit, List<Category> categories, JpaUserEntity user) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.categories = categories;
        this.user = user;
    }

    protected JpaIngredientEntity() {
    }

    public static JpaIngredientEntity fromDomain(Ingredient ingredient) {
        return new JpaIngredientEntity(
                ingredient.id().id(),
                ingredient.name(),
                ingredient.unit(),
                ingredient.categories(),
                ingredient.user() != null ? JpaUserEntity.fromDomain(ingredient.user()) : null
        );
    }

    public Ingredient toDomain() {
        return new Ingredient(
                new IngredientId(id),
                name,
                unit,
                categories,
                user != null ? user.toDomain() : null
        );
    }

    public Ingredient toDomainWithoutCategoriesAndUser() {
        return new Ingredient(
                new IngredientId(id),
                name,
                unit,
                List.of(),
                null
        );
    }

    public UUID getId() {
        return id;
    }
}
