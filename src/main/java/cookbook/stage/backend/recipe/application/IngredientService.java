package cookbook.stage.backend.recipe.application;

import cookbook.stage.backend.recipe.domain.ingredient.Ingredient;
import cookbook.stage.backend.recipe.domain.ingredient.IngredientId;
import cookbook.stage.backend.recipe.domain.ingredient.IngredientRepository;
import cookbook.stage.backend.recipe.domain.ingredient.Unit;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public List<Ingredient> getIngredientsByIds(List<IngredientId> ids) {
        return ingredientRepository.findByIds(ids).stream()
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
