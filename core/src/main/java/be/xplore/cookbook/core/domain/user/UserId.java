package be.xplore.cookbook.core.domain.user;


import be.xplore.cookbook.core.domain.exception.NotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Objects;
import java.util.UUID;

public record UserId(UUID id) {
    public UserId {
        Objects.requireNonNull(id, "User id cannot be null!");
    }

    public NotFoundException notFound() {
        return new NotFoundException("User [" + id + "] not found");
    }

    public static UserId create() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId fromJwt(Jwt jwt) {
        return new UserId(UUID.fromString(jwt.getSubject()));
    }
}
