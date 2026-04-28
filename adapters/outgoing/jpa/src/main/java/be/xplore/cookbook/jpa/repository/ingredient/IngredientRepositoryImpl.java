package be.xplore.cookbook.jpa.repository.ingredient;

import be.xplore.cookbook.core.domain.ingredient.Ingredient;

import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.repository.IngredientRepository;
import be.xplore.cookbook.jpa.repository.ingredient.entity.JpaIngredientEntity;
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
                .toDomainWithoutCategories();
    }

    @Override
    public Optional<Ingredient> findById(IngredientId id) {
        return jpaIngredientRepository.findById(id.id())
                .map(JpaIngredientEntity::toDomainWithoutCategories);
    }

    @Override
    public List<Ingredient> findByIds(List<IngredientId> ids) {
        List<UUID> uuids = ids.stream().map(IngredientId::id).toList();
        return jpaIngredientRepository.findAllById(uuids)
                .stream()
                .map(JpaIngredientEntity::toDomainWithoutCategories)
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
                .map(JpaIngredientEntity::toDomainWithoutCategories)
                .toList();
    }
}
