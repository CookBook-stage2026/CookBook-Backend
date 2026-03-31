package cookbook.stage.backend.ingredient.infrastructure;

import cookbook.stage.backend.ingredient.domain.Ingredient;
import cookbook.stage.backend.ingredient.domain.IngredientRepository;
import cookbook.stage.backend.ingredient.infrastructure.jpa.JpaIngredientEntity;
import cookbook.stage.backend.ingredient.infrastructure.jpa.JpaIngredientRepository;
import cookbook.stage.backend.ingredient.shared.IngredientId;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class IngredientRepositoryImpl implements IngredientRepository {
    private final JpaIngredientRepository ingredientRepository;

    public IngredientRepositoryImpl(JpaIngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public Ingredient save(Ingredient ingredient) {
        return this.ingredientRepository.save(JpaIngredientEntity.fromDomain(ingredient)).toDomain();
    }

    public List<Ingredient> findAll(Pageable pageable) {
        return this.ingredientRepository.findAll(pageable).stream()
                .map(JpaIngredientEntity::toDomain).toList();
    }

    public List<Ingredient> findAllByIds(List<IngredientId> ids) {
        return this.ingredientRepository.findAllById(ids.stream().map(IngredientId::id).toList()).stream().map(JpaIngredientEntity::toDomain).toList();
    }
}
