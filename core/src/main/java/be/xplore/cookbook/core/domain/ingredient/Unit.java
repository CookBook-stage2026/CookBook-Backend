package be.xplore.cookbook.core.domain.ingredient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

public enum Unit {
    GRAM,
    KILOGRAM,
    MILLILITER,
    LITER,
    TEASPOON,
    TABLESPOON,
    CUP,
    PIECE,
    PINCH;

    private static final Set<Unit> WEIGHT_UNITS = Set.of(GRAM, KILOGRAM);
    private static final Set<Unit> VOLUME_UNITS = Set.of(MILLILITER, LITER, TEASPOON, TABLESPOON, CUP);
    private static final Set<Unit> DISCRETE_UNITS = Set.of(PIECE, PINCH);

    private static final double GRAMS_PER_KILOGRAM = 1000.0;
    private static final double MILLILITERS_PER_LITER = 1000.0;
    private static final double MILLILITERS_PER_TEASPOON = 4.92892;
    private static final double MILLILITERS_PER_TABLESPOON = 14.7868;
    private static final double MILLILITERS_PER_CUP = 236.588;

    private static final int GRAM_SCALE       = 0;
    private static final int KILOGRAM_SCALE   = 2;
    private static final int MILLILITER_SCALE = 0;
    private static final int LITER_SCALE      = 2;
    private static final int DISCRETE_SCALE   = 0;

    public boolean isCompatibleWith(Unit other) {
        return categoryOf(this).equals(categoryOf(other));
    }

    public double toBaseUnit(double quantity) {
        return switch (this) {
            case GRAM -> quantity;
            case KILOGRAM -> quantity * GRAMS_PER_KILOGRAM;
            case MILLILITER -> quantity;
            case LITER -> quantity * MILLILITERS_PER_LITER;
            case TEASPOON -> quantity * MILLILITERS_PER_TEASPOON;
            case TABLESPOON -> quantity * MILLILITERS_PER_TABLESPOON;
            case CUP -> quantity * MILLILITERS_PER_CUP;
            case PIECE, PINCH -> quantity;
        };
    }

    public Unit baseUnit() {
        return switch (categoryOf(this)) {
            case "weight" -> GRAM;
            case "volume" -> MILLILITER;
            default -> this;
        };
    }

    public static NormalisedQuantity normalise(double baseQuantity, Unit baseUnit) {
        return switch (baseUnit) {
            case GRAM -> baseQuantity >= GRAMS_PER_KILOGRAM
                    ? new NormalisedQuantity(round(baseQuantity / GRAMS_PER_KILOGRAM, KILOGRAM_SCALE), KILOGRAM)
                    : new NormalisedQuantity(round(baseQuantity, GRAM_SCALE), GRAM);
            case MILLILITER -> baseQuantity >= MILLILITERS_PER_LITER
                    ? new NormalisedQuantity(round(baseQuantity / MILLILITERS_PER_LITER, LITER_SCALE), LITER)
                    : new NormalisedQuantity(round(baseQuantity, MILLILITER_SCALE), MILLILITER);
            default -> new NormalisedQuantity(round(baseQuantity, DISCRETE_SCALE), baseUnit);
        };
    }

    public static double roundToTwoDecimals(double value) {
        return round(value, 2);
    }

    private static double round(double value, int scale) {
        return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

    private static String categoryOf(Unit unit) {
        if (WEIGHT_UNITS.contains(unit)) {
            return "weight";
        }
        if (VOLUME_UNITS.contains(unit)) {
            return "volume";
        }
        if (DISCRETE_UNITS.contains(unit)) {
            return unit.name();
        }

        throw new IllegalStateException("Unhandled unit: " + unit);
    }

    public record NormalisedQuantity(double quantity, Unit unit) {
    }
}
