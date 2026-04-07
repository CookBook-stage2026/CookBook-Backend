package cookbook.stage.backend.user.domain;

import cookbook.stage.backend.user.shared.User;

import java.util.UUID;

public class SocialConnection {
    private final UUID id;
    private String provider;
    private String providerId;
    private User user;

    public SocialConnection(String provider, String providerId) {
        id = UUID.randomUUID();
        this.provider = provider;
        this.providerId = providerId;
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderId() {
        return providerId;
    }
}
