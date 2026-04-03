package cookbook.stage.backend;

import cookbook.stage.backend.ingredient.application.IngredientService;
import cookbook.stage.backend.ingredient.domain.Unit;
import cookbook.stage.backend.ingredient.shared.IngredientId;
import cookbook.stage.backend.recipe.application.RecipeService;
import cookbook.stage.backend.recipe.domain.RecipeIngredient;
import cookbook.stage.backend.recipe.shared.RecipeId;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile("dev")
@Component
public class DataInitializer implements ApplicationRunner {

    private final RecipeService recipeService;
    private final IngredientService ingredientService;

    public DataInitializer(RecipeService recipeService, IngredientService ingredientService) {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public void run(@NonNull ApplicationArguments args) {

        // Ingredients
        var spaghetti = ingredientService.createIngredient(IngredientId.create(), "Spaghetti", Unit.GRAM);
        var pancetta = ingredientService.createIngredient(IngredientId.create(),"Pancetta", Unit.GRAM);
        var eggs = ingredientService.createIngredient(IngredientId.create(),"Eggs", Unit.PIECE);
        var chickenBreast = ingredientService.createIngredient(IngredientId.create(),"Chicken breast", Unit.GRAM);
        var yogurt = ingredientService.createIngredient(IngredientId.create(),"Yogurt", Unit.GRAM);
        var tomatoSauce = ingredientService.createIngredient(IngredientId.create(),"Tomato sauce", Unit.GRAM);
        var heavyCream = ingredientService.createIngredient(IngredientId.create(),"Heavy cream", Unit.MILLILITER);
        var garamMasala = ingredientService.createIngredient(IngredientId.create(),"Garam masala", Unit.TEASPOON);
        var groundBeef = ingredientService.createIngredient(IngredientId.create(),"Ground beef", Unit.GRAM);
        var tortillas = ingredientService.createIngredient(IngredientId.create(),"Tortillas", Unit.PIECE);
        var cheddar = ingredientService.createIngredient(IngredientId.create(),"Cheddar cheese", Unit.GRAM);
        var salsa = ingredientService.createIngredient(IngredientId.create(),"Salsa", Unit.GRAM);
        var sourCream = ingredientService.createIngredient(IngredientId.create(),"Sour cream", Unit.GRAM);
        var bellPeppers = ingredientService.createIngredient(IngredientId.create(),"Bell peppers", Unit.PIECE);
        var broccoli = ingredientService.createIngredient(IngredientId.create(),"Broccoli", Unit.GRAM);
        var carrots = ingredientService.createIngredient(IngredientId.create(),"Carrots", Unit.PIECE);
        var soySauce = ingredientService.createIngredient(IngredientId.create(),"Soy sauce", Unit.TABLESPOON);
        var sesameOil = ingredientService.createIngredient(IngredientId.create(),"Sesame oil", Unit.TABLESPOON);
        var onions = ingredientService.createIngredient(IngredientId.create(),"Onions", Unit.PIECE);
        var beefBroth = ingredientService.createIngredient(IngredientId.create(),"Beef broth", Unit.LITER);
        var gruyere = ingredientService.createIngredient(IngredientId.create(),"Gruyere cheese", Unit.GRAM);
        var baguette = ingredientService.createIngredient(IngredientId.create(),"Baguette", Unit.PIECE);
        var butter = ingredientService.createIngredient(IngredientId.create(),"Butter", Unit.GRAM);
        var bananas = ingredientService.createIngredient(IngredientId.create(),"Bananas", Unit.PIECE);
        var flour = ingredientService.createIngredient(IngredientId.create(),"Flour", Unit.GRAM);
        var milk = ingredientService.createIngredient(IngredientId.create(),"Milk", Unit.MILLILITER);
        var tomatoes = ingredientService.createIngredient(IngredientId.create(),"Tomatoes", Unit.PIECE);
        var cucumber = ingredientService.createIngredient(IngredientId.create(),"Cucumber", Unit.PIECE);
        var feta = ingredientService.createIngredient(IngredientId.create(),"Feta cheese", Unit.GRAM);
        var olives = ingredientService.createIngredient(IngredientId.create(),"Kalamata olives", Unit.GRAM);
        var oliveOil = ingredientService.createIngredient(IngredientId.create(),"Olive oil", Unit.TABLESPOON);
        var beefChuck = ingredientService.createIngredient(IngredientId.create(),"Beef chuck", Unit.GRAM);
        var redWine = ingredientService.createIngredient(IngredientId.create(),"Red wine", Unit.MILLILITER);
        var mushrooms = ingredientService.createIngredient(IngredientId.create(),"Mushrooms", Unit.GRAM);
        var bacon = ingredientService.createIngredient(IngredientId.create(),"Bacon", Unit.GRAM);
        var pizzaDough = ingredientService.createIngredient(IngredientId.create(),"Pizza dough", Unit.GRAM);
        var mozzarella = ingredientService.createIngredient(IngredientId.create(),"Fresh mozzarella", Unit.GRAM);
        var basil = ingredientService.createIngredient(IngredientId.create(),"Fresh basil", Unit.GRAM);
        var darkChocolate = ingredientService.createIngredient(IngredientId.create(),"Dark chocolate", Unit.GRAM);
        var sugar = ingredientService.createIngredient(IngredientId.create(),"Sugar", Unit.GRAM);

        // Recipes
        var carbonaraId = RecipeId.create();
        recipeService.createRecipe(
                carbonaraId,
                "Spaghetti Carbonara",
                "A classic Italian pasta dish with eggs, cheese and pancetta",
                30,
                List.of("Boil pasta", "Fry pancetta", "Mix eggs and cheese", "Combine all"),
                List.of(
                        new RecipeIngredient(carbonaraId, spaghetti.id(), 200),
                        new RecipeIngredient(carbonaraId, pancetta.id(), 100),
                        new RecipeIngredient(carbonaraId, eggs.id(), 2)
                ),
                2
        );

        var tikkaId = RecipeId.create();
        recipeService.createRecipe(
                tikkaId,
                "Chicken Tikka Masala",
                "Creamy and spiced Indian curry with tender chicken",
                45,
                List.of("Marinate chicken", "Grill chicken", "Prepare tomato cream sauce", "Simmer chicken in sauce", "Serve with rice"),
                List.of(
                        new RecipeIngredient(tikkaId, chickenBreast.id(), 500),
                        new RecipeIngredient(tikkaId, yogurt.id(), 200),
                        new RecipeIngredient(tikkaId, tomatoSauce.id(), 400),
                        new RecipeIngredient(tikkaId, heavyCream.id(), 100),
                        new RecipeIngredient(tikkaId, garamMasala.id(), 2)
                ),
                4
        );

        var tacosId = RecipeId.create();
        recipeService.createRecipe(
                tacosId,
                "Beef Tacos",
                "Mexican street-style tacos with seasoned ground beef",
                25,
                List.of("Brown ground beef", "Season with spices", "Warm tortillas", "Assemble tacos with toppings"),
                List.of(
                        new RecipeIngredient(tacosId, groundBeef.id(), 400),
                        new RecipeIngredient(tacosId, tortillas.id(), 8),
                        new RecipeIngredient(tacosId, cheddar.id(), 100),
                        new RecipeIngredient(tacosId, salsa.id(), 150),
                        new RecipeIngredient(tacosId, sourCream.id(), 100)
                ),
                4
        );

        var stirFryId = RecipeId.create();
        recipeService.createRecipe(
                stirFryId,
                "Vegetable Stir Fry",
                "Quick and healthy Asian-inspired stir fry",
                20,
                List.of("Chop all vegetables", "Heat wok with oil", "Stir fry vegetables on high heat", "Add soy sauce and sesame oil", "Serve with rice"),
                List.of(
                        new RecipeIngredient(stirFryId, bellPeppers.id(), 2),
                        new RecipeIngredient(stirFryId, broccoli.id(), 200),
                        new RecipeIngredient(stirFryId, carrots.id(), 2),
                        new RecipeIngredient(stirFryId, soySauce.id(), 3),
                        new RecipeIngredient(stirFryId, sesameOil.id(), 1)
                ),
                2
        );

        var onionSoupId = RecipeId.create();
        recipeService.createRecipe(
                onionSoupId,
                "French Onion Soup",
                "Rich and hearty French classic topped with melted cheese",
                60,
                List.of("Caramelize onions for 30 minutes", "Add beef broth and simmer", "Ladle into bowls", "Top with bread and gruyere", "Broil until cheese is golden"),
                List.of(
                        new RecipeIngredient(onionSoupId, onions.id(), 4),
                        new RecipeIngredient(onionSoupId, beefBroth.id(), 1),
                        new RecipeIngredient(onionSoupId, gruyere.id(), 150),
                        new RecipeIngredient(onionSoupId, baguette.id(), 4),
                        new RecipeIngredient(onionSoupId, butter.id(), 50)
                ),
                4
        );

        var pancakesId = RecipeId.create();
        recipeService.createRecipe(
                pancakesId,
                "Banana Pancakes",
                "Fluffy pancakes with a hint of banana",
                20,
                List.of("Mash bananas", "Mix batter ingredients", "Heat pan with butter", "Pour batter and cook until bubbles form", "Flip and cook other side"),
                List.of(
                        new RecipeIngredient(pancakesId, bananas.id(), 2),
                        new RecipeIngredient(pancakesId, flour.id(), 150),
                        new RecipeIngredient(pancakesId, milk.id(), 200),
                        new RecipeIngredient(pancakesId, eggs.id(), 2),
                        new RecipeIngredient(pancakesId, butter.id(), 30)
                ),
                2
        );

        var greekSaladId = RecipeId.create();
        recipeService.createRecipe(
                greekSaladId,
                "Greek Salad",
                "Fresh Mediterranean salad with feta and olives",
                15,
                List.of("Chop tomatoes and cucumber", "Slice red onion", "Add olives and feta", "Dress with olive oil and oregano"),
                List.of(
                        new RecipeIngredient(greekSaladId, tomatoes.id(), 3),
                        new RecipeIngredient(greekSaladId, cucumber.id(), 1),
                        new RecipeIngredient(greekSaladId, feta.id(), 150),
                        new RecipeIngredient(greekSaladId, olives.id(), 100),
                        new RecipeIngredient(greekSaladId, oliveOil.id(), 3)
                ),
                2
        );

        var bourguignonId = RecipeId.create();
        recipeService.createRecipe(
                bourguignonId,
                "Beef Bourguignon",
                "Classic French braised beef stew in red wine",
                180,
                List.of("Brown beef in batches", "Sauté onions and carrots", "Add wine and broth", "Braise in oven for 2 hours", "Add mushrooms and finish"),
                List.of(
                        new RecipeIngredient(bourguignonId, beefChuck.id(), 800),
                        new RecipeIngredient(bourguignonId, redWine.id(), 500),
                        new RecipeIngredient(bourguignonId, carrots.id(), 3),
                        new RecipeIngredient(bourguignonId, mushrooms.id(), 200),
                        new RecipeIngredient(bourguignonId, bacon.id(), 150)
                ),
                6
        );

        var pizzaId = RecipeId.create();
        recipeService.createRecipe(
                pizzaId,
                "Margherita Pizza",
                "Simple Neapolitan pizza with fresh basil and mozzarella",
                40,
                List.of("Prepare dough and let rise", "Spread tomato sauce", "Add mozzarella", "Bake at high heat for 10 minutes", "Top with fresh basil"),
                List.of(
                        new RecipeIngredient(pizzaId, pizzaDough.id(), 300),
                        new RecipeIngredient(pizzaId, tomatoSauce.id(), 150),
                        new RecipeIngredient(pizzaId, mozzarella.id(), 200),
                        new RecipeIngredient(pizzaId, basil.id(), 10),
                        new RecipeIngredient(pizzaId, oliveOil.id(), 2)
                ),
                2
        );

        var lavaCakeId = RecipeId.create();
        recipeService.createRecipe(
                lavaCakeId,
                "Chocolate Lava Cake",
                "Decadent warm chocolate cake with a molten center",
                25,
                List.of("Melt chocolate and butter", "Whisk eggs and sugar", "Fold in flour", "Pour into ramekins", "Bake for 12 minutes and serve immediately"),
                List.of(
                        new RecipeIngredient(lavaCakeId, darkChocolate.id(), 150),
                        new RecipeIngredient(lavaCakeId, butter.id(), 100),
                        new RecipeIngredient(lavaCakeId, eggs.id(), 3),
                        new RecipeIngredient(lavaCakeId, sugar.id(), 80),
                        new RecipeIngredient(lavaCakeId, flour.id(), 50)
                ),
                4
        );
    }
}