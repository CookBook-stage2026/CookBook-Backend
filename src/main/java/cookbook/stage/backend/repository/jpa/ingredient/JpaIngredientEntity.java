package cookbook.stage.backend.repository.jpa.ingredient;

import cookbook.stage.backend.domain.ingredient.Category;
import cookbook.stage.backend.domain.ingredient.Ingredient;
import cookbook.stage.backend.domain.ingredient.IngredientId;
import cookbook.stage.backend.domain.ingredient.Unit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    public JpaIngredientEntity(UUID id, String name, Unit unit, Category category) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.category = category;
    }

    protected JpaIngredientEntity() {
    }

    public static JpaIngredientEntity fromDomain(Ingredient ingredient) {
        return new JpaIngredientEntity(
                ingredient.id().id(),
                ingredient.name(),
                ingredient.unit(),
                ingredient.category()
        );
    }

    public Ingredient toDomain() {
        return new Ingredient(new IngredientId(id), name, unit, category);
    }

    public UUID getId() {
        return id;
    }
}
