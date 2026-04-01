package cookbook.stage.backend.recipe.infrastructure.jpa;

import jakarta.persistence.Embeddable;

@Embeddable
public class JpaIngredient {
    private String name;
    private double quantity;
    private String unit;

    protected JpaIngredient() {
    }

    public JpaIngredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
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
