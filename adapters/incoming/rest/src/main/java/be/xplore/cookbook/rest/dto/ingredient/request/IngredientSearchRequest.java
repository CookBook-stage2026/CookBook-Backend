package be.xplore.cookbook.rest.dto.ingredient.request;

import java.util.List;
import java.util.UUID;

public record IngredientSearchRequest(
        String query,
        List<UUID> alreadySelectedIds,
        int page,
        int size,
        boolean onlyPersonal
) {
    public IngredientSearchRequest {
        alreadySelectedIds = alreadySelectedIds == null ? List.of() : List.copyOf(alreadySelectedIds);
    }
}
