package be.xplore.cookbook.core.common;

import java.util.List;

public record PagedResult<T>(List<T> content, int pageNumber, int pageSize, long totalElements) {
    public long totalPages() {
        return pageSize == 0 ? 0 : (long) Math.ceil((double) totalElements / pageSize);
    }
}
