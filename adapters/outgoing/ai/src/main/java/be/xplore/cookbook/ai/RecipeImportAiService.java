package be.xplore.cookbook.ai;

import be.xplore.cookbook.ai.dto.ImportedRecipeAiDto;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface RecipeImportAiService {

    @SystemMessage("""
            You are a recipe import assistant.
            When given a URL:
            1. Use the fetchPage tool to retrieve the page content.
            2. Extract the recipe title, description, total duration in minutes, servings, steps, and ingredients.
            3. Translate all content to English.
            4. For each ingredient, map the unit to exactly one of:\
             GRAM, KILOGRAM, MILLILITER, LITER, TEASPOON, TABLESPOON, CUP, PIECE, PINCH, NONE.
            5. Use PIECE for countable items such as cloves, eggs, and whole vegetables.
            6. Use NONE with quantity 1.0 for "to taste" or unspecified amounts.
            7. All quantities must be positive numbers greater than 0.
            """)
    ImportedRecipeAiDto importFromUrl(@UserMessage String url);
}
