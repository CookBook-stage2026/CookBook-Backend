package cookbook.stage.backend.recipe.infrastructure.jpa.ingredient;

import cookbook.stage.backend.recipe.domain.ingredient.Ingredient;
import cookbook.stage.backend.recipe.domain.ingredient.Unit;
import cookbook.stage.backend.recipe.domain.ingredient.IngredientId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "ingredients")
public class JpaIngredientEntity {
    @Id
    @Column(name = "ingredient_id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Unit unit;

    public JpaIngredientEntity(UUID id, String name, Unit unit) {
        this.id = id;
        this.name = name;
        this.unit = unit;
    }

    protected JpaIngredientEntity() {
    }

    public static JpaIngredientEntity fromDomain(Ingredient ingredient) {
        return new JpaIngredientEntity(
                ingredient.id().id(),
                ingredient.name(),
                ingredient.unit()
        );
    }

    public Ingredient toDomain() {
        return new Ingredient(new IngredientId(id), name, unit);
    }
}
