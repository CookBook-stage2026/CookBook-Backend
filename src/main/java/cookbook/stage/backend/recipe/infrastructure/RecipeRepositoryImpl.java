package cookbook.stage.backend.recipe.infrastructure;

import cookbook.stage.backend.recipe.domain.recipe.Recipe;
import cookbook.stage.backend.recipe.domain.recipe.RecipeRepository;
import cookbook.stage.backend.recipe.domain.recipe.RecipeSummary;
import cookbook.stage.backend.recipe.infrastructure.jpa.recipe.JpaRecipeEntity;
import cookbook.stage.backend.recipe.infrastructure.jpa.recipe.JpaRecipeRepository;
import cookbook.stage.backend.recipe.domain.recipe.RecipeId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RecipeRepositoryImpl implements RecipeRepository {
    private final JpaRecipeRepository jpaRecipeRepository;

    public RecipeRepositoryImpl(JpaRecipeRepository jpaRecipeRepository) {
        this.jpaRecipeRepository = jpaRecipeRepository;
    }

    @Override
    public Recipe save(Recipe recipe) {
        jpaRecipeRepository.save(JpaRecipeEntity.fromDomain(recipe));
        return recipe;
    }

    @Override
    public Optional<Recipe> findById(RecipeId id) {
        return jpaRecipeRepository.findByIdWithIngredients(id.id())
                .map(entity -> {
                    jpaRecipeRepository.findByIdWithSteps(id.id());
                    return entity.toDomain();
                });
    }

    @Override
    public Page<RecipeSummary> findAllSummaries(Pageable pageable) {
        return jpaRecipeRepository.findAll(pageable)
                .map(JpaRecipeEntity::toSummary);
    }

    @Override
    public long count() {
        return jpaRecipeRepository.count();
    }
}
