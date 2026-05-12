package be.xplore.cookbook.core.port.recipe;

public interface RecipeImportPort {
    ScrapedRecipe scrape(String url);
}
