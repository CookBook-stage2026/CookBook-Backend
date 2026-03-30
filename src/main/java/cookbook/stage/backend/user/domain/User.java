package cookbook.stage.backend.user.domain;

import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.ArrayList;
import java.util.List;

@AggregateRoot
public class User {
    @Identity
    private final UserId id;
    private String emailAddress;
    private String firstName;
    private String lastName;
    private List<SavedRecipe> savedRecipes;

    public User(UserId id, String emailAddress, String firstName, String lastName, List<SavedRecipe> savedRecipes) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.firstName = firstName;
        this.lastName = lastName;
        this.savedRecipes = savedRecipes;
    }

    public static User createUser(String email, String firstname, String lastName){
        return new User(UserId.create(), email, firstname, lastName, new ArrayList<>());
    }

    public UserId getId() {
        return id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<SavedRecipe> getSavedRecipes() {
        return savedRecipes;
    }
}
