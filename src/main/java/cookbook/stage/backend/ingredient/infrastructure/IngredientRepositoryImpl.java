package cookbook.stage.backend.ingredient.infrastructure;

import cookbook.stage.backend.ingredient.domain.Ingredient;
import cookbook.stage.backend.ingredient.domain.IngredientRepository;
import cookbook.stage.backend.ingredient.infrastructure.jpa.JpaIngredientEntity;
import cookbook.stage.backend.ingredient.infrastructure.jpa.JpaIngredientRepository;
import cookbook.stage.backend.ingredient.shared.IngredientId;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class IngredientRepositoryImpl implements IngredientRepository {
    private final JpaIngredientRepository jpaIngredientRepository;

    public IngredientRepositoryImpl(JpaIngredientRepository jpaIngredientRepository) {
        this.jpaIngredientRepository = jpaIngredientRepository;
    }

    public Ingredient save(Ingredient ingredient) {
        return this.jpaIngredientRepository.save(JpaIngredientEntity.fromDomain(ingredient))
                .toDomain();
    }

    public List<Ingredient> findAll(Pageable pageable) {
        return this.jpaIngredientRepository.findAll(pageable).stream()
                .map(JpaIngredientEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Ingredient> findById(IngredientId id) {
        return jpaIngredientRepository.findById(id.id())
                .map(JpaIngredientEntity::toDomain);
    }

    @Override
    public List<Ingredient> findByIds(List<IngredientId> ids) {
        List<UUID> uuids = ids.stream().map(IngredientId::id).toList();
        return jpaIngredientRepository.findAllById(uuids)
                .stream()
                .map(JpaIngredientEntity::toDomain)
                .toList();
    }

    @Override
    public List<Ingredient> searchByName(String name, Pageable pageable) {
        return this.jpaIngredientRepository.findByNameContainingIgnoreCase(name, pageable).stream()
                .map(JpaIngredientEntity::toDomain)
                .toList();
    }
}
