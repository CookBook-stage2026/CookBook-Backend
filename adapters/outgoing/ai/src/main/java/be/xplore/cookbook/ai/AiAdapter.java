package be.xplore.cookbook.ai;

import be.xplore.cookbook.ai.dto.DaySuggestionInput;
import be.xplore.cookbook.ai.dto.RecipeInput;
import be.xplore.cookbook.ai.dto.SuggestedWeekScheduleIds;
import be.xplore.cookbook.ai.dto.WeekSuggestionInput;
import be.xplore.cookbook.ai.exception.AiConnectionException;
import be.xplore.cookbook.ai.exception.AiInvalidResponseException;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.recipe.RecipeSummary;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;
import be.xplore.cookbook.core.port.recipe.ImportedIngredient;
import be.xplore.cookbook.core.port.recipe.ImportedRecipe;
import be.xplore.cookbook.core.port.recipe.RecipeImportPort;
import be.xplore.cookbook.core.port.recipe.RecipeSuggestionsPort;
import be.xplore.cookbook.core.port.recipe.SuggestedRecipeEnhancement;
import be.xplore.cookbook.core.port.recipe.SuggestedRecipeId;
import be.xplore.cookbook.core.port.weekschedule.ScheduleSuggestionsPort;
import be.xplore.cookbook.core.port.weekschedule.SuggestedDayRecipe;
import com.fasterxml.jackson.core.JsonParseException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

@Component
public class AiAdapter implements RecipeSuggestionsPort, ScheduleSuggestionsPort, RecipeImportPort {

    private static final int PREVIOUS_WEEK_INDEX = 0;
    private static final int CURRENT_WEEK_INDEX = 1;
    private static final int NEXT_WEEK_INDEX = 2;

    private final RecipeAiService recipeAiService;
    private final ScheduleAiService scheduleAiService;
    private final JsonMapper jsonMapper;
    private final Logger logger = Logger.getLogger(AiAdapter.class.getName());

    public AiAdapter(RecipeAiService recipeAiService, ScheduleAiService scheduleAiService,
                     JsonMapper jsonMapper) {
        this.recipeAiService = recipeAiService;
        this.scheduleAiService = scheduleAiService;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public SuggestedRecipeEnhancement enhanceRecipe(Recipe recipe) {
        String recipeJson = serialize(RecipeInput.fromDomain(recipe));
        try {
            return recipeAiService.enhanceRecipe(recipeJson);
        } catch (RuntimeException e) {
            throw handleException(e);
        }
    }

    @Override
    public RecipeId suggestRecipeForDay(DayOfWeek dayToSuggestFor, List<WeekSchedule> weekSchedules,
                                        List<RecipeSummary> availableRecipes) {
        DaySuggestionInput input = buildDaySuggestionInput(dayToSuggestFor, weekSchedules, availableRecipes);
        String daySuggestionJson = serialize(input);
        try {
            SuggestedRecipeId suggestedRecipeId = scheduleAiService.suggestRecipeForDay(daySuggestionJson);
            return new RecipeId(suggestedRecipeId.id());
        } catch (RuntimeException e) {
            throw handleException(e);
        }
    }

    @Override
    public List<SuggestedDayRecipe> suggestWeekSchedule(LocalDate weekStartDate, List<WeekSchedule> weekSchedules,
                                                        List<RecipeSummary> availableRecipes) {
        WeekSuggestionInput input = buildWeekSuggestionInput(weekStartDate, weekSchedules, availableRecipes);
        String json = serialize(input);
        try {
            SuggestedWeekScheduleIds result = scheduleAiService.suggestWeekSchedule(json);
            return result.days().stream()
                    .map(entry -> new SuggestedDayRecipe(entry.day(), new RecipeId(entry.recipeId())))
                    .toList();
        } catch (RuntimeException e) {
            throw handleException(e);
        }
    }

    @Override
    public ImportedRecipe scrape(String url) {
        try {
            var result = recipeAiService.importFromUrl(url);

            List<ImportedIngredient> ingredients = result.ingredients().stream()
                    .map(i -> new ImportedIngredient(i.name(), i.unit(), i.quantity(),
                            i.categories()))
                    .toList();

            return new ImportedRecipe(
                    result.title(),
                    result.description(),
                    result.durationInMinutes(),
                    result.servings(),
                    result.steps(),
                    ingredients
            );
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException && !(cause instanceof JsonParseException)) {
                throw new AiConnectionException("AI service is unavailable", e);
            }
            logger.warning(e.getLocalizedMessage());
            throw new AiInvalidResponseException("AI returned an unexpected response", e);
        }
    }

    private String serialize(Object input) {
        try {
            return jsonMapper.writeValueAsString(input);
        } catch (Exception e) {
            throw new AiInvalidResponseException("Failed to serialize input for AI", e);
        }
    }

    private RuntimeException handleException(RuntimeException e) {
        if (e.getCause() instanceof IOException) {
            return new AiConnectionException("AI service is unavailable", e);
        }
        logger.warning(e.getLocalizedMessage());
        return new AiInvalidResponseException("AI returned an unexpected response", e);
    }

    private WeekSuggestionInput buildWeekSuggestionInput(LocalDate weekStartDate, List<WeekSchedule> weekSchedules,
                                                     List<RecipeSummary> availableRecipes) {
        return WeekSuggestionInput.fromDomain(
                weekStartDate,
                availableRecipes,
                weekSchedules.get(PREVIOUS_WEEK_INDEX),
                weekSchedules.get(CURRENT_WEEK_INDEX),
                weekSchedules.get(NEXT_WEEK_INDEX)
        );
    }

    private DaySuggestionInput buildDaySuggestionInput(DayOfWeek dayToSuggestFor, List<WeekSchedule> weekSchedules,
                                                       List<RecipeSummary> availableRecipes) {
        WeekSchedule previousWeek = weekSchedules.get(PREVIOUS_WEEK_INDEX);
        WeekSchedule currentWeek = weekSchedules.get(CURRENT_WEEK_INDEX);
        WeekSchedule nextWeek = weekSchedules.get(NEXT_WEEK_INDEX);
        return DaySuggestionInput.fromDomain(dayToSuggestFor, availableRecipes, previousWeek, currentWeek, nextWeek);
    }
}
