package cookbook.stage.backend.repository;

import cookbook.stage.backend.domain.ingredient.IngredientId;
import cookbook.stage.backend.domain.recipe.Recipe;
import cookbook.stage.backend.domain.recipe.RecipeRepository;
import cookbook.stage.backend.domain.recipe.RecipeSummary;
import cookbook.stage.backend.repository.jpa.recipe.JpaRecipeEntity;
import cookbook.stage.backend.repository.jpa.recipe.JpaRecipeRepository;
import cookbook.stage.backend.domain.recipe.RecipeId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RecipeRepositoryImpl implements RecipeRepository {
    private final JpaRecipeRepository jpaRecipeRepository;

    public RecipeRepositoryImpl(JpaRecipeRepository jpaRecipeRepository) {
        this.jpaRecipeRepository = jpaRecipeRepository;
    }

    @Override
    public Recipe save(Recipe recipe) {
        JpaRecipeEntity saved = jpaRecipeRepository.save(JpaRecipeEntity.fromDomain(recipe));
        return jpaRecipeRepository.findById(saved.getId())
                .map(JpaRecipeEntity::toDomain)
                .orElseThrow(() -> new IllegalStateException("Recipe not found after save: " + saved.getId()));
    }

    @Override
    public Optional<Recipe> findById(RecipeId id) {
        return jpaRecipeRepository.findById(id.id())
                .map(entity -> {
                    jpaRecipeRepository.findById(id.id());
                    return entity.toDomain();
                });
    }

    @Override
    public Page<RecipeSummary> findAllSummariesWithFilter(List<IngredientId> ingredientIds, Pageable pageable) {
        if (ingredientIds == null || ingredientIds.isEmpty()) {
            return jpaRecipeRepository.findAll(pageable)
                    .map(JpaRecipeEntity::toSummary);
        }

        List<UUID> uuids = ingredientIds.stream()
                .map(IngredientId::id)
                .toList();

        return jpaRecipeRepository.findByIngredients(uuids, uuids.size(), pageable)
                .map(JpaRecipeEntity::toSummary);
    }

    @Override
    public long count() {
        return jpaRecipeRepository.count();
    }
}
