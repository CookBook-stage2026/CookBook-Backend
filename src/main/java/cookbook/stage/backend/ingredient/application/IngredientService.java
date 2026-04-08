package cookbook.stage.backend.ingredient.application;

import cookbook.stage.backend.ingredient.domain.Ingredient;
import cookbook.stage.backend.ingredient.domain.IngredientRepository;
import cookbook.stage.backend.ingredient.shared.Unit;
import cookbook.stage.backend.ingredient.shared.IngredientApiDto;
import cookbook.stage.backend.ingredient.shared.IngredientId;
import cookbook.stage.backend.ingredient.shared.IngredientsApi;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientService implements IngredientsApi {
    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    public List<IngredientApiDto> getIngredientsByIds(List<IngredientId> ids) {
        return ingredientRepository.findByIds(ids).stream()
                .map(IngredientApiDto::fromDomain)
                .toList();
    }

    public List<Ingredient> findAll(Pageable pageable) {
        return this.ingredientRepository.findAll(pageable);
    }

    public Ingredient createIngredient(String name, Unit unit) {
        Ingredient ingredient = new Ingredient(IngredientId.create(), name, unit);
        return ingredientRepository.save(ingredient);
    }

    public List<Ingredient> searchByName(String name, Pageable pageable) {
        return this.ingredientRepository.searchByName(name, pageable);
    }
}
