package be.xplore.cookbook.jpa.repository.recipe;

import be.xplore.cookbook.core.common.PagedResult;
import be.xplore.cookbook.core.common.Paging;
import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.recipe.RecipeSummary;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.user.UserPreferences;
import be.xplore.cookbook.core.repository.RecipeRepository;
import be.xplore.cookbook.jpa.repository.recipe.entity.JpaRecipeEntity;
import org.springframework.data.domain.PageRequest;
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
    public PagedResult<RecipeSummary> findAllSummariesWithFilter(
            List<IngredientId> ingredientIds,
            UserPreferences preferences,
            UserId userId,
            Paging paging
    ) {
        List<UUID> ingredientUuids = ingredientIds.stream()
                .map(IngredientId::id)
                .toList();

        List<UUID> excludedIngredientUuids = preferences.excludedIngredients().stream()
                .map(i -> i.id().id())
                .toList();

        List<Category> excludedCategories = preferences.excludedCategories();
        Pageable pageable = PageRequest.of(paging.page(), paging.size());

        var page = jpaRecipeRepository.findAllSummariesWithFilterByCreatorId(
                ingredientUuids,
                excludedIngredientUuids,
                excludedCategories,
                userId.id(),
                pageable
        );

        return new PagedResult<>(
                page.getContent().stream()
                        .map(JpaRecipeEntity::toSummary)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    @Override
    public List<RecipeSummary> querySummaries(Paging paging, UserId userId, String query) {
        Pageable pageable = PageRequest.of(paging.page(), paging.size());

        return jpaRecipeRepository.searchByNamePrioritizingStartsWith(query, userId.id(), pageable)
                .stream().map(JpaRecipeEntity::toSummary).toList();
    }

    @Override
    public long count() {
        return jpaRecipeRepository.count();
    }
}
