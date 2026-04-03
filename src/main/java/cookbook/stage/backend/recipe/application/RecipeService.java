package cookbook.stage.backend.recipe.application;

import cookbook.stage.backend.recipe.domain.Ingredient;
import cookbook.stage.backend.recipe.domain.Recipe;
import cookbook.stage.backend.recipe.domain.RecipeRepository;
import cookbook.stage.backend.recipe.domain.RecipeSummary;
import cookbook.stage.backend.recipe.shared.RecipeId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Recipe createRecipe(String name, String description, int durationInMinutes,
                               List<String> steps, List<Ingredient> ingredients) {
        Recipe recipe = new Recipe(RecipeId.create(), name, description, durationInMinutes, steps, ingredients);

        return recipeRepository.save(recipe);
    }

    public Page<RecipeSummary> findAllSummaries(Pageable pageable) {
        return recipeRepository.findAllSummaries(pageable);
    }
}
