package cookbook.stage.backend.ingredient.infrastructure.jpa;

import cookbook.stage.backend.ingredient.domain.Ingredient;
import cookbook.stage.backend.ingredient.domain.Unit;
import cookbook.stage.backend.ingredient.shared.IngredientId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "ingredients")
public class JpaIngredientEntity {
    @Id
    @Column(name = "ingredient_id")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column
    private Unit unit;

    public JpaIngredientEntity(UUID id, String name, String description, Unit unit) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.unit = unit;
    }

    protected JpaIngredientEntity() {
    }

    public static JpaIngredientEntity fromDomain(Ingredient ingredient) {
        return new JpaIngredientEntity(
                ingredient.id().id(),
                ingredient.name(),
                ingredient.description(),
                ingredient.unit().orElse(null)
        );
    }

    public Ingredient toDomain() {
        return new Ingredient(new IngredientId(id), name, description, Optional.ofNullable(unit));
    }
}
