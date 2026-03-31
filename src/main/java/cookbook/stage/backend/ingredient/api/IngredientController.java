package cookbook.stage.backend.ingredient.api;

import cookbook.stage.backend.ingredient.api.dto.IngredientDto;
import cookbook.stage.backend.ingredient.application.IngredientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ingredients")
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
    public ResponseEntity<List<IngredientDto>> getAllIngredients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ingredientService.findAll(pageable));
    }

    /**
     * Adds a new ingredient (with or without a unit) to the database
     *
     * @param ingredientDto The new ingredient, without an id
     * @return whether the ingredient was successfully added
     */
    @PostMapping
    public ResponseEntity<IngredientDto> createIngredient(
            @Valid @RequestBody IngredientDto ingredientDto
    ) {
        var ingredient = ingredientService.createIngredient(
                ingredientDto.name(),
                ingredientDto.description(),
                ingredientDto.unit()
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ingredient);
    }
}
