package cookbook.stage.backend.domain.user;

import java.util.UUID;

public class SocialConnection {
    private final UUID id;
    private String provider;
    private String providerId;

    public SocialConnection(String provider, String providerId) {
        id = UUID.randomUUID();
        this.provider = provider;
        this.providerId = providerId;
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
