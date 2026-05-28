package be.xplore.cookbook.core.repository;

import be.xplore.cookbook.core.common.PagedResult;
import be.xplore.cookbook.core.common.Paging;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository {
    Ingredient save(Ingredient ingredient);

    Optional<Ingredient> findById(IngredientId id);

    Optional<Ingredient> findByIdWithoutCategoriesAndUser(IngredientId id);

    List<Ingredient> findByIds(List<IngredientId> ids);

    PagedResult<Ingredient> searchPersonalByNameExcludingIds(String name, List<IngredientId> excludedIds, Paging paging,
                                                     User user);

    PagedResult<Ingredient> searchByNameExcludingIds(String name, List<IngredientId> excludedIds, Paging paging,
                                                     User user);

    Optional<Ingredient> findByNameIgnoreCaseGlobalOrUser(String name, User user);

    Optional<Ingredient> findByNameIgnoreCaseAndUser(String name, User user);

    void delete(Ingredient ingredient);
}
