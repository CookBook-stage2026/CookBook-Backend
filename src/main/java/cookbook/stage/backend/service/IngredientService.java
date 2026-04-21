package cookbook.stage.backend.service;

import cookbook.stage.backend.domain.ingredient.Ingredient;
import cookbook.stage.backend.domain.ingredient.IngredientId;
import cookbook.stage.backend.domain.ingredient.IngredientRepository;
import cookbook.stage.backend.domain.ingredient.Unit;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
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

    @Transactional
    public Ingredient createIngredient(String name, Unit unit) {
        Ingredient ingredient = new Ingredient(IngredientId.create(), name, unit);
        return ingredientRepository.save(ingredient);
    }

    public List<Ingredient> searchByName(String name, Pageable pageable) {
        return this.ingredientRepository.searchByName(name, pageable);
    }
}
