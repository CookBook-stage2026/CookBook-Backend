package be.xplore.cookbook.core.domain.user;

import java.util.UUID;

public record SocialConnection(
        UUID id,
        String provider,
        String providerId) {

    public SocialConnection(String provider, String providerId) {
        this(UUID.randomUUID(), provider, providerId);
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
