package cookbook.stage.backend.recipe.api;

import cookbook.stage.backend.recipe.api.result.IngredientDto;
import cookbook.stage.backend.recipe.application.IngredientService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    /**
     * Gets all ingredients with pagination
     *
     * @param page current page (default 0)
     * @param size size of page (default 20)
     * @return List of ingredients
     */
    @GetMapping
    public List<IngredientDto> getAllIngredients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return ingredientService.findAll(pageable).stream()
                .map(IngredientDto::fromDomain)
                .toList();
    }

    /**
     * Gets ingredients by name, with pagination (case-insensitive + doesn't matter where the letters are in the name)
     * @param query the name
     * @param page current page (default 0)
     * @param size size of page (default 10)
     * @return List of ingredients matching the query
     */
    @GetMapping("/search")
    public List<IngredientDto> getIngredientByName(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return ingredientService.searchByName(query, pageable).stream()
                .map(IngredientDto::fromDomain)
                .toList();
    }
}
