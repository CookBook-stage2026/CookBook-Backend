package be.xplore.cookbook.core.port.recipe;

public interface RecipeImportPort {
    ImportedRecipe scrape(String url);
}
