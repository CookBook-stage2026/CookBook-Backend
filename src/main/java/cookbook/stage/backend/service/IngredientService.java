package cookbook.stage.backend.service;

import cookbook.stage.backend.domain.ingredient.Ingredient;
import cookbook.stage.backend.domain.ingredient.IngredientId;
import cookbook.stage.backend.domain.ingredient.IngredientRepository;
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

    public List<Ingredient> searchByName(String name, List<IngredientId> selectedIds, Pageable pageable) {
        return this.ingredientRepository.searchByName(name, selectedIds, pageable);
    }
}
