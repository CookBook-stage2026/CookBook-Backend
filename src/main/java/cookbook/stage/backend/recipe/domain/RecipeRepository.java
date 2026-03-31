package cookbook.stage.backend.recipe.domain;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecipeRepository {
    Recipe save(Recipe recipe);

    List<Recipe> findAll(Pageable pageable);

    void deleteAll();
}
