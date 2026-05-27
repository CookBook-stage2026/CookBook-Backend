package be.xplore.cookbook.core.domain.recipe;

import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.Unit;

import java.util.List;
import java.util.Objects;

public record RecipeIngredient(Ingredient ingredient, double baseQuantity, Unit unit) {
    public RecipeIngredient {
        Objects.requireNonNull(ingredient, "Ingredient cannot be null!");
        if (baseQuantity <= 0) {
            throw new IllegalArgumentException("Base quantity must be greater than 0!");
        }
        Objects.requireNonNull(unit, "Unit cannot be null!");
    }

    public static List<RecipeIngredient> merge(RecipeIngredient a, RecipeIngredient b) {
        if (!a.ingredient().name().equals(b.ingredient().name())) {
            return List.of(a, b);
        }

        if (a.unit() == b.unit()) {
            Unit.NormalisedQuantity normalised =
                    Unit.normalise(a.baseQuantity() + b.baseQuantity(), a.unit().baseUnit());
            return List.of(new RecipeIngredient(a.ingredient(), normalised.quantity(), normalised.unit()));
        }

        if (a.unit().isCompatibleWith(b.unit())) {
            double mergedBase = a.unit().toBaseUnit(a.baseQuantity()) + b.unit().toBaseUnit(b.baseQuantity());
            Unit.NormalisedQuantity normalised = Unit.normalise(mergedBase, a.unit().baseUnit());
            return List.of(new RecipeIngredient(a.ingredient(), normalised.quantity(), normalised.unit()));
        }

        if (a.ingredient.user() != null) {
            return List.of(a, new RecipeIngredient(a.ingredient(), b.baseQuantity(), b.unit()));
        }

        return List.of(new RecipeIngredient(b.ingredient(), a.baseQuantity(), a.unit()), b);
    }
}
