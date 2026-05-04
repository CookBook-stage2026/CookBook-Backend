package be.xplore.cookbook.core.domain.user;

public record User(UserId id, String email, String displayName, String provider, String providerId) {

    public User(String email, String displayName, String provider, String providerId) {
        this(UserId.create(), email, displayName, provider, providerId);
    }

}
