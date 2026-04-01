package cookbook.stage.backend.recipe.infrastructure.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "ingredients")
public class JpaIngredient {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double quantity;

    private String unit;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private JpaRecipeEntity recipe;

    protected JpaIngredient() {
    }

    public JpaIngredient(String name, double quantity, String unit, JpaRecipeEntity recipe) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.recipe = recipe;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }
}
