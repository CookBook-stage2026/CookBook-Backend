package cookbook.stage.backend.repository;

import cookbook.stage.backend.domain.ingredient.Category;
import cookbook.stage.backend.domain.ingredient.IngredientId;
import cookbook.stage.backend.domain.recipe.Recipe;
import cookbook.stage.backend.domain.recipe.RecipeId;
import cookbook.stage.backend.domain.recipe.RecipeRepository;
import cookbook.stage.backend.domain.recipe.RecipeSummary;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.domain.user.UserPreferences;
import cookbook.stage.backend.repository.jpa.recipe.JpaRecipeEntity;
import cookbook.stage.backend.repository.jpa.recipe.JpaRecipeRepository;
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
    public Optional<Recipe> findById(RecipeId id, UserId userId) {
        return jpaRecipeRepository.findByIdAndUserId(id.id(), userId.id())
                .map(JpaRecipeEntity::toDomain);
    }

    @Override
    public Page<RecipeSummary> findAllSummariesWithFilter(List<IngredientId> ingredientIds, UserPreferences preferences,
                                                          UserId userId, Pageable pageable) {
        List<UUID> ingredientUuids = ingredientIds.stream()
                          .map(IngredientId::id)
                          .toList();

        List<UUID> excludedIngredientUuids = preferences.excludedIngredients().stream()
                          .map(i -> i.id().id())
                          .toList();

        List<Category> excludedCategories = preferences.excludedCategories();

        return jpaRecipeRepository.findAllSummariesWithFilterByCreatorId(
                ingredientUuids,
                excludedIngredientUuids,
                excludedCategories,
                userId.id(),
                pageable
        ).map(JpaRecipeEntity::toSummary);
    }

    @Override
    public List<RecipeSummary> querySummaries(Pageable pageable, UserId userId, String query) {
        return jpaRecipeRepository.searchByNamePrioritizingStartsWith(query, userId.id(), pageable)
                .stream().map(JpaRecipeEntity::toSummary).toList();
    }

    @Override
    public List<RecipeSummary> searchSummariesByName(Pageable pageable, UserId userId, String query) {
        return jpaRecipeRepository.searchByNamePrioritizingStartsWith(query, userId.id(), pageable)
                .stream().map(JpaRecipeEntity::toSummary).toList();
    }

    @Override
    public long count() {
        return jpaRecipeRepository.count();
    }
}
