package cookbook.stage.backend;

import cookbook.stage.backend.recipe.domain.Ingredient;
import cookbook.stage.backend.recipe.domain.Recipe;
import cookbook.stage.backend.recipe.domain.RecipeRepository;
import cookbook.stage.backend.recipe.shared.RecipeId;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {
    private static final String BUTTER = "Butter";

    private final RecipeRepository recipeRepository;

    public DataInitializer(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public void run(ApplicationArguments args) {

        recipeRepository.save(new Recipe(
                RecipeId.create(),
                "Spaghetti Carbonara Carbonara Carbonara Carbonara Carbonara Carbonara Carbonara Carbonara Carbonara Carbonara Carbonara Carbonara Carbonara",
                "A classic Italian pasta dish with eggs, cheese and pancetta A classic Italian pasta dish with eggs, cheese and pancetta A classic Italian pasta dish with eggs, cheese and pancetta A classic Italian pasta dish with eggs, cheese and pancetta",
                30,
                List.of("Boil pasta", "Fry pancetta", "Mix eggs and cheese", "Combine all"),
                List.of(
                        new Ingredient("Spaghetti", 200, "gram"),
                        new Ingredient("Pancetta", 100, "gram"),
                        new Ingredient("Eggs", 2, null)
                )
        ));

        recipeRepository.save(new Recipe(
                RecipeId.create(),
                "Chicken Tikka Masala",
                "Creamy and spiced Indian curry with tender chicken",
                45,
                List.of("Marinate chicken", "Grill chicken", "Prepare tomato cream sauce", "Simmer chicken in sauce", "Serve with rice"),
                List.of(
                        new Ingredient("Chicken breast", 500, "gram"),
                        new Ingredient("Yogurt", 200, "gram"),
                        new Ingredient("Tomato sauce", 400, "gram"),
                        new Ingredient("Heavy cream", 100, "ml"),
                        new Ingredient("Garam masala", 2, "tsp")
                )
        ));

        recipeRepository.save(new Recipe(
                RecipeId.create(),
                "Beef Tacos",
                "Mexican street-style tacos with seasoned ground beef",
                25,
                List.of("Brown ground beef", "Season with spices", "Warm tortillas", "Assemble tacos with toppings"),
                List.of(
                        new Ingredient("Ground beef", 400, "gram"),
                        new Ingredient("Tortillas", 8, null),
                        new Ingredient("Cheddar cheese", 100, "gram"),
                        new Ingredient("Salsa", 150, "gram"),
                        new Ingredient("Sour cream", 100, "gram")
                )
        ));

        recipeRepository.save(new Recipe(
                RecipeId.create(),
                "Vegetable Stir Fry",
                "Quick and healthy Asian-inspired stir fry",
                20,
                List.of("Chop all vegetables", "Heat wok with oil", "Stir fry vegetables on high heat", "Add soy sauce and sesame oil", "Serve with rice"),
                List.of(
                        new Ingredient("Bell peppers", 2, null),
                        new Ingredient("Broccoli", 200, "gram"),
                        new Ingredient("Carrots", 2, null),
                        new Ingredient("Soy sauce", 3, "tbsp"),
                        new Ingredient("Sesame oil", 1, "tbsp")
                )
        ));

        recipeRepository.save(new Recipe(
                RecipeId.create(),
                "French Onion Soup",
                "Rich and hearty French classic topped with melted cheese",
                60,
                List.of("Caramelize onions for 30 minutes", "Add beef broth and simmer", "Ladle into bowls", "Top with bread and gruyere", "Broil until cheese is golden"),
                List.of(
                        new Ingredient("Onions", 4, null),
                        new Ingredient("Beef broth", 1, "liter"),
                        new Ingredient("Gruyere cheese", 150, "gram"),
                        new Ingredient("Baguette", 4, null),
                        new Ingredient(BUTTER, 50, "gram")
                )
        ));

        recipeRepository.save(new Recipe(
                RecipeId.create(),
                "Banana Pancakes",
                "Fluffy pancakes with a hint of banana",
                20,
                List.of("Mash bananas", "Mix batter ingredients", "Heat pan with butter", "Pour batter and cook until bubbles form", "Flip and cook other side"),
                List.of(
                        new Ingredient("Bananas", 2, null),
                        new Ingredient("Flour", 150, "gram"),
                        new Ingredient("Milk", 200, "ml"),
                        new Ingredient("Eggs", 2, null),
                        new Ingredient(BUTTER, 30, "gram")
                )
        ));

        recipeRepository.save(new Recipe(
                RecipeId.create(),
                "Greek Salad",
                "Fresh Mediterranean salad with feta and olives",
                15,
                List.of("Chop tomatoes and cucumber", "Slice red onion", "Add olives and feta", "Dress with olive oil and oregano"),
                List.of(
                        new Ingredient("Tomatoes", 3, null),
                        new Ingredient("Cucumber", 1, null),
                        new Ingredient("Feta cheese", 150, "gram"),
                        new Ingredient("Kalamata olives", 100, "gram"),
                        new Ingredient("Olive oil", 3, "tbsp")
                )
        ));

        recipeRepository.save(new Recipe(
                RecipeId.create(),
                "Beef Bourguignon",
                "Classic French braised beef stew in red wine",
                180,
                List.of("Brown beef in batches", "Sauté onions and carrots", "Add wine and broth", "Braise in oven for 2 hours", "Add mushrooms and finish"),
                List.of(
                        new Ingredient("Beef chuck", 800, "gram"),
                        new Ingredient("Red wine", 500, "ml"),
                        new Ingredient("Carrots", 3, null),
                        new Ingredient("Mushrooms", 200, "gram"),
                        new Ingredient("Bacon", 150, "gram")
                )
        ));

        recipeRepository.save(new Recipe(
                RecipeId.create(),
                "Margherita Pizza",
                "Simple Neapolitan pizza with fresh basil and mozzarella",
                40,
                List.of("Prepare dough and let rise", "Spread tomato sauce", "Add mozzarella", "Bake at high heat for 10 minutes", "Top with fresh basil"),
                List.of(
                        new Ingredient("Pizza dough", 300, "gram"),
                        new Ingredient("Tomato sauce", 150, "gram"),
                        new Ingredient("Fresh mozzarella", 200, "gram"),
                        new Ingredient("Fresh basil", 10, "gram"),
                        new Ingredient("Olive oil", 2, "tbsp")
                )
        ));

        recipeRepository.save(new Recipe(
                RecipeId.create(),
                "Chocolate Lava Cake",
                "Decadent warm chocolate cake with a molten center",
                25,
                List.of("Melt chocolate and butter", "Whisk eggs and sugar", "Fold in flour", "Pour into ramekins", "Bake for 12 minutes and serve immediately"),
                List.of(
                        new Ingredient("Dark chocolate", 150, "gram"),
                        new Ingredient(BUTTER, 100, "gram"),
                        new Ingredient("Eggs", 3, null),
                        new Ingredient("Sugar", 80, "gram"),
                        new Ingredient("Flour", 50, "gram")
                )
        ));
    }
}
