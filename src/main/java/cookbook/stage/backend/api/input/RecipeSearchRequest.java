package cookbook.stage.backend.api.input;

import java.util.List;
import java.util.UUID;

public record RecipeSearchRequest(
        List<UUID> ingredientIds,
        Boolean applyPreferences,
        Integer page,
        Integer size
) {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    public RecipeSearchRequest {
        ingredientIds = ingredientIds == null ? List.of() : List.copyOf(ingredientIds);

        applyPreferences = applyPreferences == null || applyPreferences;

        page = (page == null || page < 0) ? DEFAULT_PAGE : page;

        size = (size == null || size <= 0) ? DEFAULT_SIZE : size;
    }
}
