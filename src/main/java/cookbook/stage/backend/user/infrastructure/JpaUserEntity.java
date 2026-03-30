package cookbook.stage.backend.user.infrastructure;

import cookbook.stage.backend.user.domain.SavedRecipe;
import cookbook.stage.backend.user.domain.User;
import cookbook.stage.backend.user.domain.UserId;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class JpaUserEntity {

    @Id
    @Column
    private UUID id;

    @Column(name = "email_address", nullable = false, unique = true)
    private String emailAddress;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private List<JpaSavedRecipeEntity> savedRecipes = new ArrayList<>();

    protected JpaUserEntity() {
    }

    public JpaUserEntity(UUID id, String emailAddress, String firstName, String lastName) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static JpaUserEntity fromDomain(User user) {
        JpaUserEntity entity = new JpaUserEntity(
                user.getId().id(),
                user.getEmailAddress(),
                user.getFirstName(),
                user.getLastName()
        );

        List<JpaSavedRecipeEntity> savedRecipeEntities = user.getSavedRecipes().stream()
                .map(JpaSavedRecipeEntity::fromDomain)
                .collect(Collectors.toList());

        entity.setSavedRecipes(savedRecipeEntities);

        return entity;
    }

    public User toDomain() {
        List<SavedRecipe> savedRecipes = this.savedRecipes.stream()
                .map(JpaSavedRecipeEntity::toDomain)
                .collect(Collectors.toList());

        return new User(
                new UserId(this.id),
                this.emailAddress,
                this.firstName,
                this.lastName,
                savedRecipes
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<JpaSavedRecipeEntity> getSavedRecipes() {
        return savedRecipes;
    }

    public void setSavedRecipes(List<JpaSavedRecipeEntity> savedRecipes) {
        this.savedRecipes = savedRecipes;
    }
}