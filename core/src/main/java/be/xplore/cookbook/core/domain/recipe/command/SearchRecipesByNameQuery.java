package be.xplore.cookbook.core.domain.recipe.command;

import be.xplore.cookbook.core.common.Paging;
import be.xplore.cookbook.core.domain.user.UserId;

public record SearchRecipesByNameQuery(Paging paging, UserId userId, String query) {
}
