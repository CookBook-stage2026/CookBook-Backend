package be.xplore.cookbook.core.domain.recipe;

public enum RecipeSortingOptions {
    DURATION("durationInMinutes"),
    NAME("name"),
    CREATOR("user.displayName");

    private final String fieldName;

    RecipeSortingOptions(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
