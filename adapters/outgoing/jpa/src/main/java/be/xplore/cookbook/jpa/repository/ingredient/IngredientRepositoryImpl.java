package be.xplore.cookbook.jpa.repository.ingredient;

import be.xplore.cookbook.core.common.PagedResult;
import be.xplore.cookbook.core.common.Paging;
import be.xplore.cookbook.core.domain.ingredient.Ingredient;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.repository.IngredientRepository;
import be.xplore.cookbook.jpa.repository.ingredient.entity.JpaIngredientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        return jpaIngredientRepository.findWithUserById(id.id())
                .map(JpaIngredientEntity::toDomain);
    }

    @Override
    public Optional<Ingredient> findByIdWithoutCategoriesAndUser(IngredientId id) {
        return jpaIngredientRepository.findById(id.id())
                .map(JpaIngredientEntity::toDomainWithoutUserAndCategories);
    }

    @Override
    public List<Ingredient> findByIds(List<IngredientId> ids) {
        List<UUID> uuids = ids.stream().map(IngredientId::id).toList();
        return jpaIngredientRepository.findAllById(uuids)
                .stream()
                .map(JpaIngredientEntity::toDomainWithoutUserAndCategories)
                .toList();
    }

    @Override
    public PagedResult<Ingredient> searchPersonalByNameExcludingIds(String name, List<IngredientId> selectedIds,
                                                                    Paging paging, User user) {
        Pageable pageable = PageRequest.of(paging.page(), paging.size());
        Page<JpaIngredientEntity> page = jpaIngredientRepository
                .searchPersonalByNamePrioritizingStartsWith(
                        toSearchName(name), toUuids(selectedIds), user.id().id(), pageable);
        return new PagedResult<>(
                page.getContent().stream()
                        .map(JpaIngredientEntity::toDomainWithoutUser)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    @Override
    public PagedResult<Ingredient> searchByNameExcludingIds(String name, List<IngredientId> selectedIds,
                                                            Paging paging, User user) {
        Pageable pageable = PageRequest.of(paging.page(), paging.size());
        Page<JpaIngredientEntity> page = jpaIngredientRepository
                .searchByNamePrioritizingStartsWith(
                        toSearchName(name), toUuids(selectedIds), user.id().id(), pageable);
        return new PagedResult<>(
                page.getContent().stream()
                        .map(JpaIngredientEntity::toDomainWithoutUserAndCategories)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    @Override
    public Optional<Ingredient> findByNameIgnoreCaseGlobalOrUser(String name, User user) {
        return jpaIngredientRepository.findExactByNameIgnoreCaseGlobalOrUser(name, user.id().id())
                .map(JpaIngredientEntity::toDomainWithoutUserAndCategories);
    }

    @Override
    public Optional<Ingredient> findByNameIgnoreCaseAndUser(String name, User user) {
        return jpaIngredientRepository.findExactByNameIgnoreCaseAndUser_Id(name, user.id().id())
                .map(JpaIngredientEntity::toDomainWithoutUserAndCategories);
    }

    @Override
    public void delete(Ingredient ingredient) {
        jpaIngredientRepository.delete(JpaIngredientEntity.fromDomain(ingredient));
    }

    private String toSearchName(String name) {
        return name != null ? name : "";
    }

    private List<UUID> toUuids(List<IngredientId> ids) {
        return ids != null
                ? ids.stream().map(IngredientId::id).toList()
                : List.of();
    }
}
