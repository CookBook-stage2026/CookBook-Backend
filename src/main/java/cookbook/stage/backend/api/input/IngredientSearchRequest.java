package cookbook.stage.backend.api.input;

import java.util.List;
import java.util.UUID;

public record IngredientSearchRequest(
        String query,
        List<UUID> alreadySelectedIds,
        Integer page,
        Integer size
) {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;

    public IngredientSearchRequest {
        alreadySelectedIds = alreadySelectedIds == null ? List.of() : List.copyOf(alreadySelectedIds);

        page = (page == null || page < 0) ? DEFAULT_PAGE : page;

        size = (size == null || size <= 0) ? DEFAULT_SIZE : size;
    }
}
