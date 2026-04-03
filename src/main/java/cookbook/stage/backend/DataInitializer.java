package cookbook.stage.backend;

import cookbook.stage.backend.ingredient.domain.Ingredient;
import cookbook.stage.backend.ingredient.domain.IngredientRepository;
import cookbook.stage.backend.ingredient.domain.Unit;
import cookbook.stage.backend.ingredient.shared.IngredientId;
import cookbook.stage.backend.recipe.domain.Recipe;
import cookbook.stage.backend.recipe.domain.RecipeIngredient;
import cookbook.stage.backend.recipe.domain.RecipeRepository;
import cookbook.stage.backend.recipe.shared.RecipeId;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Profile("dev")
@Component
public class DataInitializer implements ApplicationRunner {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    public DataInitializer(RecipeRepository recipeRepository, IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public void run(@NonNull ApplicationArguments args) {
        // Ingredients
        var spaghetti = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Spaghetti", Unit.GRAM));
        var pancetta = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Pancetta", Unit.GRAM));
        var eggs = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Eggs", Unit.PIECE));
        var chickenBreast = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Chicken breast", Unit.GRAM));
        var yogurt = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Yogurt", Unit.GRAM));
        var tomatoSauce = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Tomato sauce", Unit.GRAM));
        var heavyCream = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Heavy cream", Unit.MILLILITER));
        var garamMasala = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Garam masala", Unit.TEASPOON));
        var groundBeef = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Ground beef", Unit.GRAM));
        var tortillas = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Tortillas", Unit.PIECE));
        var cheddar = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Cheddar cheese", Unit.GRAM));
        var salsa = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Salsa", Unit.GRAM));
        var sourCream = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Sour cream", Unit.GRAM));
        var bellPeppers = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Bell peppers", Unit.PIECE));
        var broccoli = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Broccoli", Unit.GRAM));
        var carrots = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Carrots", Unit.PIECE));
        var soySauce = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Soy sauce", Unit.TABLESPOON));
        var sesameOil = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Sesame oil", Unit.TABLESPOON));
        var onions = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Onions", Unit.PIECE));
        var beefBroth = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Beef broth", Unit.LITER));
        var gruyere = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Gruyere cheese", Unit.GRAM));
        var baguette = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Baguette", Unit.PIECE));
        var butter = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Butter", Unit.GRAM));
        var bananas = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Bananas", Unit.PIECE));
        var flour = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Flour", Unit.GRAM));
        var milk = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Milk", Unit.MILLILITER));
        var tomatoes = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Tomatoes", Unit.PIECE));
        var cucumber = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Cucumber", Unit.PIECE));
        var feta = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Feta cheese", Unit.GRAM));
        var olives = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Kalamata olives", Unit.GRAM));
        var oliveOil = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Olive oil", Unit.TABLESPOON));
        var beefChuck = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Beef chuck", Unit.GRAM));
        var redWine = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Red wine", Unit.MILLILITER));
        var mushrooms = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Mushrooms", Unit.GRAM));
        var bacon = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Bacon", Unit.GRAM));
        var pizzaDough = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Pizza dough", Unit.GRAM));
        var mozzarella = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Fresh mozzarella", Unit.GRAM));
        var basil = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Fresh basil", Unit.GRAM));
        var darkChocolate = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Dark chocolate", Unit.GRAM));
        var sugar = ingredientRepository.save(
                new Ingredient(new IngredientId(UUID.randomUUID()), "Sugar", Unit.GRAM));

        // Recipes
        recipeRepository.save(new Recipe(
                new RecipeId(UUID.randomUUID()),
                "Spaghetti Carbonara",
                "A classic Italian pasta dish with eggs, cheese and pancetta",
                30,
                List.of("Boil pasta", "Fry pancetta", "Mix eggs and cheese", "Combine all"),
                List.of(
                        new RecipeIngredient(spaghetti.id(), 200),
                        new RecipeIngredient(pancetta.id(), 100),
                        new RecipeIngredient(eggs.id(), 2)
                ),
                2
        ));

        recipeRepository.save(new Recipe(
                new RecipeId(UUID.randomUUID()),
                "Chicken Tikka Masala",
                "Creamy and spiced Indian curry with tender chicken",
                45,
                List.of("Marinate chicken", "Grill chicken", "Prepare tomato cream sauce", "Simmer chicken in sauce", "Serve with rice"),
                List.of(
                        new RecipeIngredient(chickenBreast.id(), 500),
                        new RecipeIngredient(yogurt.id(), 200),
                        new RecipeIngredient(tomatoSauce.id(), 400),
                        new RecipeIngredient(heavyCream.id(), 100),
                        new RecipeIngredient(garamMasala.id(), 2)
                ),
                4
        ));

        recipeRepository.save(new Recipe(
                new RecipeId(UUID.randomUUID()),
                "Beef Tacos",
                "Mexican street-style tacos with seasoned ground beef",
                25,
                List.of("Brown ground beef", "Season with spices", "Warm tortillas", "Assemble tacos with toppings"),
                List.of(
                        new RecipeIngredient(groundBeef.id(), 400),
                        new RecipeIngredient(tortillas.id(), 8),
                        new RecipeIngredient(cheddar.id(), 100),
                        new RecipeIngredient(salsa.id(), 150),
                        new RecipeIngredient(sourCream.id(), 100)
                ),
                4
        ));

        recipeRepository.save(new Recipe(
                new RecipeId(UUID.randomUUID()),
                "Vegetable Stir Fry",
                "Quick and healthy Asian-inspired stir fry",
                20,
                List.of("Chop all vegetables", "Heat wok with oil", "Stir fry vegetables on high heat", "Add soy sauce and sesame oil", "Serve with rice"),
                List.of(
                        new RecipeIngredient(bellPeppers.id(), 2),
                        new RecipeIngredient(broccoli.id(), 200),
                        new RecipeIngredient(carrots.id(), 2),
                        new RecipeIngredient(soySauce.id(), 3),
                        new RecipeIngredient(sesameOil.id(), 1)
                ),
                2
        ));

        recipeRepository.save(new Recipe(
                new RecipeId(UUID.randomUUID()),
                "French Onion Soup",
                "Rich and hearty French classic topped with melted cheese",
                60,
                List.of("Caramelize onions for 30 minutes", "Add beef broth and simmer", "Ladle into bowls", "Top with bread and gruyere", "Broil until cheese is golden"),
                List.of(
                        new RecipeIngredient(onions.id(), 4),
                        new RecipeIngredient(beefBroth.id(), 1),
                        new RecipeIngredient(gruyere.id(), 150),
                        new RecipeIngredient(baguette.id(), 4),
                        new RecipeIngredient(butter.id(), 50)
                ),
                4
        ));

        recipeRepository.save(new Recipe(
                new RecipeId(UUID.randomUUID()),
                "Banana Pancakes",
                "Fluffy pancakes with a hint of banana",
                20,
                List.of("Mash bananas", "Mix batter ingredients", "Heat pan with butter", "Pour batter and cook until bubbles form", "Flip and cook other side"),
                List.of(
                        new RecipeIngredient(bananas.id(), 2),
                        new RecipeIngredient(flour.id(), 150),
                        new RecipeIngredient(milk.id(), 200),
                        new RecipeIngredient(eggs.id(), 2),
                        new RecipeIngredient(butter.id(), 30)
                ),
                2
        ));

        recipeRepository.save(new Recipe(
                new RecipeId(UUID.randomUUID()),
                "Greek Salad",
                "Fresh Mediterranean salad with feta and olives",
                15,
                List.of("Chop tomatoes and cucumber", "Slice red onion", "Add olives and feta", "Dress with olive oil and oregano"),
                List.of(
                        new RecipeIngredient(tomatoes.id(), 3),
                        new RecipeIngredient(cucumber.id(), 1),
                        new RecipeIngredient(feta.id(), 150),
                        new RecipeIngredient(olives.id(), 100),
                        new RecipeIngredient(oliveOil.id(), 3)
                ),
                2
        ));

        recipeRepository.save(new Recipe(
                new RecipeId(UUID.randomUUID()),
                "Beef Bourguignon",
                "Classic French braised beef stew in red wine",
                180,
                List.of("Brown beef in batches", "Sauté onions and carrots", "Add wine and broth", "Braise in oven for 2 hours", "Add mushrooms and finish"),
                List.of(
                        new RecipeIngredient(beefChuck.id(), 800),
                        new RecipeIngredient(redWine.id(), 500),
                        new RecipeIngredient(carrots.id(), 3),
                        new RecipeIngredient(mushrooms.id(), 200),
                        new RecipeIngredient(bacon.id(), 150)
                ),
                6
        ));

        recipeRepository.save(new Recipe(
                new RecipeId(UUID.randomUUID()),
                "Margherita Pizza",
                "Simple Neapolitan pizza with fresh basil and mozzarella",
                40,
                List.of("Prepare dough and let rise", "Spread tomato sauce", "Add mozzarella", "Bake at high heat for 10 minutes", "Top with fresh basil"),
                List.of(
                        new RecipeIngredient(pizzaDough.id(), 300),
                        new RecipeIngredient(tomatoSauce.id(), 150),
                        new RecipeIngredient(mozzarella.id(), 200),
                        new RecipeIngredient(basil.id(), 10),
                        new RecipeIngredient(oliveOil.id(), 2)
                ),
                2
        ));

        recipeRepository.save(new Recipe(
                new RecipeId(UUID.randomUUID()),
                "Chocolate Lava Cake",
                "Decadent warm chocolate cake with a molten center",
                25,
                List.of("Melt chocolate and butter", "Whisk eggs and sugar", "Fold in flour", "Pour into ramekins", "Bake for 12 minutes and serve immediately"),
                List.of(
                        new RecipeIngredient(darkChocolate.id(), 150),
                        new RecipeIngredient(butter.id(), 100),
                        new RecipeIngredient(eggs.id(), 3),
                        new RecipeIngredient(sugar.id(), 80),
                        new RecipeIngredient(flour.id(), 50)
                ),
                4
        ));
    }
}
