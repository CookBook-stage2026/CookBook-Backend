package cookbook.stage.backend.ingredient.application;

import cookbook.stage.backend.ingredient.api.dto.IngredientDto;
import cookbook.stage.backend.ingredient.domain.Ingredient;
import cookbook.stage.backend.ingredient.domain.IngredientRepository;
import cookbook.stage.backend.ingredient.domain.Unit;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public IngredientDto createIngredient(String name, String description, Unit unit) {
        Ingredient newIngredient;

        if (unit != null) {
            newIngredient = Ingredient.createIngredient(name, description, unit);
        } else {
            newIngredient = Ingredient.createIngredient(name, description);
        }

        return IngredientDto.fromDomain(this.ingredientRepository.save(newIngredient));
    }

    public List<IngredientDto> findAll(Pageable pageable) {
        return this.ingredientRepository.findAll(pageable).stream().map(IngredientDto::fromDomain).toList();
    }
}
