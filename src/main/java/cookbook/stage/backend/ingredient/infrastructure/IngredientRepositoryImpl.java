package cookbook.stage.backend.ingredient.infrastructure;

import cookbook.stage.backend.ingredient.domain.Ingredient;
import cookbook.stage.backend.ingredient.domain.IngredientRepository;
import cookbook.stage.backend.ingredient.infrastructure.jpa.JpaIngredientEntity;
import cookbook.stage.backend.ingredient.infrastructure.jpa.JpaIngredientRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IngredientRepositoryImpl implements IngredientRepository {
    private final JpaIngredientRepository jpaIngredientRepository;

    public IngredientRepositoryImpl(JpaIngredientRepository jpaIngredientRepository) {
        this.jpaIngredientRepository = jpaIngredientRepository;
    }

    public Ingredient save(Ingredient ingredient) {
        return this.jpaIngredientRepository.save(JpaIngredientEntity.fromDomain(ingredient)).toDomain();
    }

    public List<Ingredient> findAll(Pageable pageable) {
        return this.jpaIngredientRepository.findAll(pageable).stream()
                .map(JpaIngredientEntity::toDomain).toList();
    }

    @Override
    public void deleteAll() {
        jpaIngredientRepository.deleteAll();
    }
}
