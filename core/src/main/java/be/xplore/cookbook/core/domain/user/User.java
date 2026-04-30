package be.xplore.cookbook.core.domain.user;

import java.util.List;

public record User(UserId id, String email, String displayName, List<SocialConnection> socialConnections) {

    public User(String email, String displayName, List<SocialConnection> socialConnections) {
        this(UserId.create(), email, displayName, socialConnections);
    }

}
