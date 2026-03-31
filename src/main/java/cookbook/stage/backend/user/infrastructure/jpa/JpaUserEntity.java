package cookbook.stage.backend.user.infrastructure.jpa;

import cookbook.stage.backend.user.domain.SavedRecipe;
import cookbook.stage.backend.user.domain.User;
import cookbook.stage.backend.user.shared.UserId;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class JpaUserEntity {
    @Id
    @Column(name = "user_id")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String emailAddress;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<JpaSavedRecipeEntity> savedRecipes = new ArrayList<>();

    public JpaUserEntity(UUID id, String emailAddress, String firstName, String lastName) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    protected JpaUserEntity() {
    }

    public UUID getId() {
        return id;
    }

    public static JpaUserEntity fromDomain(User user) {
        JpaUserEntity jpaUser = new JpaUserEntity(
                user.getId().id(),
                user.getEmailAddress(),
                user.getFirstName(),
                user.getLastName()
        );

        List<JpaSavedRecipeEntity> savedRecipeEntities = user.getSavedRecipes().stream()
                .map(recipe -> JpaSavedRecipeEntity.fromDomain(recipe, jpaUser))
                .toList();

        jpaUser.savedRecipes.addAll(savedRecipeEntities);

        return jpaUser;
    }

    public User toDomain() {
        List<SavedRecipe> domainSavedRecipes = this.savedRecipes.stream()
                .map(JpaSavedRecipeEntity::toDomain)
                .toList();

        return new User(
                new UserId(this.id),
                this.emailAddress,
                this.firstName,
                this.lastName,
                domainSavedRecipes
        );
    }
}
