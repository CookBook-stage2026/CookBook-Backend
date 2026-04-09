package cookbook.stage.backend.repository;

import cookbook.stage.backend.domain.ingredient.Ingredient;
import cookbook.stage.backend.domain.ingredient.IngredientRepository;
import cookbook.stage.backend.repository.jpa.ingredient.JpaIngredientEntity;
import cookbook.stage.backend.repository.jpa.ingredient.JpaIngredientRepository;
import cookbook.stage.backend.domain.ingredient.IngredientId;
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
    public List<Ingredient> searchByName(String name, List<IngredientId> selectedIds, Pageable pageable) {
        String searchName = name != null ? name : "";
        List<UUID> selectedUuids = selectedIds != null
                ? selectedIds.stream().map(IngredientId::id).toList()
                : List.of();

        return this.jpaIngredientRepository
                .searchByNamePrioritizingStartsWith(searchName, selectedUuids, pageable)
                .stream()
                .map(JpaIngredientEntity::toDomain)
                .toList();
    }
}
