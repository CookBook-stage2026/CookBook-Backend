package be.xplore.cookbook.ai;

import be.xplore.cookbook.ai.dto.RecipeInput;
import be.xplore.cookbook.core.ai.AiPort;
import be.xplore.cookbook.core.ai.EnhancedRecipeSuggestion;
import be.xplore.cookbook.core.domain.exception.AiResponseParsingException;
import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.Unit;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.logging.Logger;

@Component
public class OllamaAdapter implements AiPort {

    private final OllamaClient ollamaClient;
    private final JsonMapper mapper;
    private final Logger logger;

    public OllamaAdapter(OllamaClient ollamaClient, JsonMapper mapper) {
        this.ollamaClient = ollamaClient;
        this.mapper = mapper;
        this.logger = Logger.getLogger(OllamaAdapter.class.getName());
    }

    @Override
    public EnhancedRecipeSuggestion enhanceRecipe(Recipe recipe) {
        String jsonInput = buildRecipeJsonInput(recipe);
        String prompt = buildEnhanceRecipePrompt(jsonInput);
        String response = ollamaClient.chat(prompt);
        logger.info(response);
        return parseEnhanceRecipeResponse(response);
    }

    private String buildRecipeJsonInput(Recipe recipe) {
        try {
            RecipeInput input = RecipeInput.fromDomain(recipe);

            return mapper.writeValueAsString(input);
        } catch (Exception e) {
            throw new AiResponseParsingException("Failed to serialize recipe for AI", e);
        }
    }

    private String buildEnhanceRecipePrompt(String jsonInput) {
        return """
                You are a professional chef. Please enhance the following recipe.
                Suggest minor culinary adjustments for better flavor, by adding 1 or 2 ingredients and update the steps.
                \s
                Instructions:
                    1. Add a new ingredient to the ingredients list. Determine appropriate 'quantity', 'unit', \s
                    and 'categories' based on culinary best practices.
                    2. Fully update the 'steps' array to seamlessly incorporate the preparation and cooking of \s
                    this new ingredient.
                    3. Adjust the quantities of existing ingredients if necessary to maintain flavor balance.
                \s
                Units: GRAM,KILOGRAM, MILLILITER, LITER, TEASPOON, TABLESPOON, CUP, PIECE, PINCH
                Categories: VEGETABLE, FRUIT, MEAT, POULTRY, FISH, SHELLFISH, EGG, DAIRY, GRAIN, LEGUME, NUT, \s
                SEED, HERB, SPICE, OIL_FAT, SWEETENER, FUNGI, SEAWEED, SAUCE_CONDIMENT, BEVERAGE, ADDITIVE
                \s
                Respond ONLY with a valid JSON object matching this exact structure:
                {
                    "durationInMinutes": 0,
                    "newIngredient": {
                        "name": "string",
                        "quantity": 0.0,
                        "unit": "string",
                        "categories": ["string"]
                    },
                    "updatedSteps": ["string", "string"]
                }
                Do not include any markdown formatting or explanations.
                \s
                Input Recipe:
                %s
                """.formatted(jsonInput);
    }

    private EnhancedRecipeSuggestion parseEnhanceRecipeResponse(String response) {
        try {
            var node = mapper.readTree(response);
            var newIngredient = node.get("newIngredient");

            String name = newIngredient.get("name").asString();
            double quantity = newIngredient.get("quantity").asDouble();
            Unit unit = Unit.valueOf(newIngredient.get("unit").asString());
            List<Category> categories = mapper.convertValue(
                    newIngredient.get("categories"),
                    mapper.getTypeFactory().constructCollectionType(List.class, Category.class)
            );

            List<String> updatedSteps = mapper.convertValue(
                    node.get("updatedSteps"),
                    mapper.getTypeFactory().constructCollectionType(List.class, String.class)
            );

            int durationInMinutes = node.get("durationInMinutes").asInt();

            return new EnhancedRecipeSuggestion(
                    durationInMinutes,
                    new EnhancedRecipeSuggestion.NewAiIngredient(
                            name,
                            quantity,
                            unit,
                            categories
                    ),
                    updatedSteps);
        } catch (Exception e) {
            throw new AiResponseParsingException("Failed to parse AI response", e);
        }
    }
}
