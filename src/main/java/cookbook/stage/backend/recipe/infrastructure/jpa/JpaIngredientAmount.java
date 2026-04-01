package cookbook.stage.backend.recipe.infrastructure.jpa;

import jakarta.persistence.Embeddable;

@Embeddable
public class JpaIngredientAmount {
    private double quantity;
    private String unit;

    protected JpaIngredientAmount() {
    }

    public JpaIngredientAmount(double quantity, String unit) {
        this.quantity = quantity;
        this.unit = unit;
    }

    public double quantity() {
        return quantity;
    }

    public String unit() {
        return unit;
    }
}
