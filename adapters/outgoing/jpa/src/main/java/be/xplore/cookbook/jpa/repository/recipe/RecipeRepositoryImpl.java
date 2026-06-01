package be.xplore.cookbook.jpa.repository.recipe;

import be.xplore.cookbook.core.common.PagedResult;
import be.xplore.cookbook.core.common.Paging;
import be.xplore.cookbook.core.common.SortDirection;
import be.xplore.cookbook.core.domain.ingredient.Category;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.recipe.RecipeSortingOptions;
import be.xplore.cookbook.core.domain.recipe.RecipeSummary;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.user.UserPreferences;
import be.xplore.cookbook.core.repository.RecipeRepository;
import be.xplore.cookbook.jpa.repository.recipe.entity.JpaRecipeEntity;
import be.xplore.cookbook.jpa.repository.user.entity.JpaUserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public Optional<Recipe> findById(RecipeId id, User user) {
        return jpaRecipeRepository.findByIdAndAccessibleByUser(id.id(), JpaUserEntity.fromDomain(user))
                .map(JpaRecipeEntity::toDomain);
    }

    @Override
    public Optional<Recipe> findOwnById(RecipeId id, User user) {
        return jpaRecipeRepository.findByIdAndUserId(id.id(), user.id().id())
                .map(JpaRecipeEntity::toDomain);
    }

    @Override
    public PagedResult<RecipeSummary> findAllSummariesWithFilter(
            List<IngredientId> ingredientIds,
            UserPreferences preferences,
            boolean includeAccessibleRecipes,
            User user,
            Paging paging,
            RecipeSortingOptions sortBy,
            SortDirection sortDirection
    ) {
        Sort.Direction direction = "descending".equalsIgnoreCase(sortDirection.name())
                ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        List<UUID> ingredientUuids = ingredientIds.stream()
                .map(IngredientId::id)
                .toList();

        List<UUID> excludedIngredientUuids = preferences.excludedIngredients().stream()
                .map(i -> i.id().id())
                .toList();

        List<Category> excludedCategories = preferences.excludedCategories();
        Pageable pageable = PageRequest.of(paging.page(), paging.size(),
                Sort.by(direction, sortBy.getFieldName()));

        JpaUserEntity jpaUser = JpaUserEntity.fromDomain(user);

        Page<JpaRecipeEntity> page = includeAccessibleRecipes
                ? jpaRecipeRepository.findAllSummariesWithFilterIncludingHousehold(
                ingredientUuids, excludedIngredientUuids, excludedCategories, jpaUser, pageable)
                : jpaRecipeRepository.findAllSummariesWithFilterOwnOnly(
                ingredientUuids, excludedIngredientUuids, excludedCategories, jpaUser, pageable);

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
    public List<RecipeSummary> findAllSummariesByUserIds(List<UserId> userIds) {
        return jpaRecipeRepository.findByUser_IdInAndIsPublicTrue(userIds.stream().map(UserId::id).toList()).stream()
                .map(JpaRecipeEntity::toSummary)
                .toList();
    }

    @Override
    public List<RecipeSummary> queryPersonalSummaries(Paging paging, User user, String query) {
        Pageable pageable = PageRequest.of(paging.page(), paging.size());

        return jpaRecipeRepository
                .searchOwnByNamePrioritizingStartsWith(query, JpaUserEntity.fromDomain(user), pageable)
                .stream()
                .map(JpaRecipeEntity::toSummary)
                .toList();
    }

    @Override
    public List<RecipeSummary> querySummaries(Paging paging, List<UserId> userIds, String query) {
        Pageable pageable = PageRequest.of(paging.page(), paging.size());
        List<UUID> userUuids = userIds.stream()
                .map(UserId::id)
                .toList();

        return jpaRecipeRepository.searchByNamePrioritizingStartsWith(query, userUuids, pageable)
                .stream()
                .map(JpaRecipeEntity::toSummary)
                .toList();
    }

    @Override
    public long count() {
        return jpaRecipeRepository.count();
    }

    @Override
    public void delete(Recipe recipe) {
        jpaRecipeRepository.delete(JpaRecipeEntity.fromDomain(recipe));
    }

    @Override
    public List<Recipe> findRecipesContainingIngredient(IngredientId ingredientId) {
        return jpaRecipeRepository.findRecipesContainingIngredient(ingredientId.id())
                .stream()
                .map(JpaRecipeEntity::toDomain)
                .toList();
    }
}
