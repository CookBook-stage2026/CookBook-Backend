package be.xplore.cookbook.rest.dto.response;

import java.util.List;

public record PaginatedResponse<T>(
        List<T> content,
        PageMetadata page
) {
    public record PageMetadata(
            int number,
            int size,
            long totalElements,
            long totalPages
    ) {
    }
}
