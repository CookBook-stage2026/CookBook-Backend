package cookbook.stage.backend.recipe.infrastructure;

import cookbook.stage.backend.recipe.domain.Recipe;
import cookbook.stage.backend.recipe.domain.RecipeRepository;
import cookbook.stage.backend.recipe.infrastructure.jpa.JpaRecipeEntity;
import cookbook.stage.backend.recipe.infrastructure.jpa.JpaRecipeRepository;
import cookbook.stage.backend.recipe.shared.RecipeId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RecipeRepositoryImpl implements RecipeRepository {
    private final JpaRecipeRepository jpaRecipeRepository;

    public RecipeRepositoryImpl(JpaRecipeRepository jpaRecipeRepository) {
        this.jpaRecipeRepository = jpaRecipeRepository;
    }

    @Override
    public Recipe save(Recipe recipe) {
        return jpaRecipeRepository.save(JpaRecipeEntity.fromDomain(recipe))
                .toDomain();
    }

    @Override
    public Optional<Recipe> findById(RecipeId id) {
        return jpaRecipeRepository.findById(id.id()).map(JpaRecipeEntity::toDomain);
    }
}
