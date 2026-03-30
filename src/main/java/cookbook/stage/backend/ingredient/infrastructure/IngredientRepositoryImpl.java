package cookbook.stage.backend.ingredient.infrastructure;

import cookbook.stage.backend.ingredient.infrastructure.jpa.JpaIngredientRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class IngredientRepositoryImpl {
    private final JpaIngredientRepository ingredientRepository;

    public IngredientRepositoryImpl(JpaIngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    
}
