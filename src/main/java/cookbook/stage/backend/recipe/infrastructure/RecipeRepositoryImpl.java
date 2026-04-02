package cookbook.stage.backend.recipe.infrastructure;

import cookbook.stage.backend.recipe.domain.Recipe;
import cookbook.stage.backend.recipe.domain.RecipeRepository;
import cookbook.stage.backend.recipe.infrastructure.jpa.JpaRecipeEntity;
import cookbook.stage.backend.recipe.infrastructure.jpa.JpaRecipeRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public List<Recipe> findAll(Pageable pageable) {
        return jpaRecipeRepository.findAllWithStepsAndIngredients(pageable).stream()
                .map(JpaRecipeEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteAll() {
        jpaRecipeRepository.deleteAll();
    }
}
