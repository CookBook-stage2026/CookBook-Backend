package cookbook.stage.backend.recipe.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecipeRepository {
    Recipe save(Recipe recipe);

    Page<RecipeSummary> findAllSummaries(Pageable pageable);

    void deleteAll();

    long count();
}
