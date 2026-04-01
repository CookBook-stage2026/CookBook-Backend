package cookbook.stage.backend.recipe.application;

import cookbook.stage.backend.recipe.api.dto.IngredientDto;
import cookbook.stage.backend.recipe.domain.IngredientAmount;
import cookbook.stage.backend.recipe.domain.Recipe;
import cookbook.stage.backend.recipe.domain.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Recipe createRecipe(String name, String description, int durationInMinutes,
                               List<String> steps, Map<String, IngredientDto> ingredients) {
        Map<String, IngredientAmount> domainIngredients = ingredients.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new IngredientAmount(e.getValue().quantity(), e.getValue().unit())
                ));

        Recipe recipe = Recipe.createRecipe(name, description, durationInMinutes, steps, domainIngredients);

        return recipeRepository.save(recipe);
    }
}
