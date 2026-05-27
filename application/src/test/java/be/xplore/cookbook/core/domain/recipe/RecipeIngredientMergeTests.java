package be.xplore.cookbook.core.domain.recipe;

import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.user.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RecipeIngredientMergeTests {

    private static final int QUANTITY_ONE = 1;
    private static final int QUANTITY_LOW = 200;
    private static final int QUANTITY_HIGH = 900;

    private static final double GRAMS_PER_KILOGRAM = 1000.0;
    private static final double MILLILITERS_PER_LITER = 1000.0;

    private RecipeIngredient ingredient(String name, double quantity, Unit unit, User user) {
        Ingredient ingredient = new Ingredient(IngredientId.create(), name, Unit.GRAM, List.of(Category.SPICE), user);
        return new RecipeIngredient(ingredient, quantity, unit);
    }

    private RecipeIngredient ingredient(String name, double quantity, Unit unit) {
        return ingredient(name, quantity, unit, null);
    }

    @Test
    void shouldNotMerge_whenDifferentIngredients() {
        RecipeIngredient a = ingredient("Sugar", QUANTITY_LOW, Unit.GRAM);
        RecipeIngredient b = ingredient("Salt", QUANTITY_LOW, Unit.GRAM);

        List<RecipeIngredient> result = RecipeIngredient.merge(a, b);

        assertThat(result).containsExactly(a, b);
    }

    @Test
    void shouldMerge_whenSameUnitAndSameName() {
        User user = new User("test@test.com", "test", "provider", "provider");

        RecipeIngredient a = ingredient("Sugar", QUANTITY_LOW, Unit.GRAM, user);
        RecipeIngredient b = ingredient("Sugar", QUANTITY_LOW, Unit.GRAM);

        List<RecipeIngredient> result = RecipeIngredient.merge(a, b);

        assertThat(result).hasSize(1);

        RecipeIngredient merged = result.getFirst();
        assertThat(merged.ingredient().id()).isEqualTo(a.ingredient().id());
        assertThat(merged.unit()).isEqualTo(Unit.GRAM);
        assertThat(merged.baseQuantity()).isEqualTo(QUANTITY_LOW + QUANTITY_LOW);
    }

    @Test
    void shouldMergeCompatibleWeightUnits() {
        User user = new User("test@test.com", "test", "provider", "provider");

        RecipeIngredient a = ingredient("Flour", QUANTITY_HIGH, Unit.GRAM);
        RecipeIngredient b = ingredient("Flour", QUANTITY_ONE, Unit.KILOGRAM, user);

        List<RecipeIngredient> result = RecipeIngredient.merge(a, b);

        assertThat(result).hasSize(1);

        RecipeIngredient merged = result.getFirst();

        assertThat(merged.ingredient().id()).isEqualTo(b.ingredient().id());
        assertThat(merged.unit()).isEqualTo(Unit.KILOGRAM);
        assertThat(merged.baseQuantity()).isEqualTo(QUANTITY_ONE + (QUANTITY_HIGH / GRAMS_PER_KILOGRAM));
    }

    @Test
    void shouldMergeCompatibleVolumeUnits() {
        User user = new User("test@test.com", "test", "provider", "provider");

        RecipeIngredient a = ingredient("Milk", QUANTITY_ONE, Unit.LITER);
        RecipeIngredient b = ingredient("Milk", QUANTITY_HIGH, Unit.MILLILITER, user);

        List<RecipeIngredient> result = RecipeIngredient.merge(a, b);

        assertThat(result).hasSize(1);

        RecipeIngredient merged = result.getFirst();

        assertThat(merged.ingredient().id()).isEqualTo(b.ingredient().id());
        assertThat(merged.unit()).isEqualTo(Unit.LITER);
        assertThat(merged.baseQuantity()).isEqualTo(QUANTITY_ONE + (QUANTITY_HIGH / MILLILITERS_PER_LITER));
    }

    @Test
    void shouldKeepBoth_whenIncompatibleUnitsAndUserExists() {
        User user = new User("test@test.com", "test", "provider", "provider");

        RecipeIngredient a = ingredient("Sugar", QUANTITY_LOW, Unit.GRAM, user);
        RecipeIngredient b = ingredient("Sugar", QUANTITY_ONE, Unit.PIECE);

        List<RecipeIngredient> result = RecipeIngredient.merge(a, b);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).ingredient().id()).isEqualTo(a.ingredient().id());
        assertThat(result.get(1).ingredient().id()).isEqualTo(a.ingredient().id());
        assertThat(result.get(0).unit()).isEqualTo(Unit.GRAM);
        assertThat(result.get(1).unit()).isEqualTo(Unit.PIECE);
    }
}
