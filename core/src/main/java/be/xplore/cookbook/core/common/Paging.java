package be.xplore.cookbook.core.common;

public record Paging(
        int page,
        int size
) {
    public static Paging unpaged() {
        return new Paging(0, Integer.MAX_VALUE);
    }
}
