package be.xplore.cookbook.core.domain.user;

import java.util.List;

public class User {

    private final UserId id;
    private String email;
    private String displayName;
    private List<SocialConnection> socialConnections;

    public User(String email, String displayName, List<SocialConnection> socialConnections) {
        this.id = UserId.create();
        this.email = email;
        this.displayName = displayName;
        this.socialConnections = socialConnections;
    }

    public User(UserId id, String email, String displayName, List<SocialConnection> socialConnections) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.socialConnections = socialConnections;
    }

    public UserId getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<SocialConnection> getSocialConnections() {
        return socialConnections;
    }
}
