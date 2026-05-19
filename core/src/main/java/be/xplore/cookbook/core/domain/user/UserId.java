package be.xplore.cookbook.core.domain.user;


import be.xplore.cookbook.core.domain.exception.UserNotFoundException;

import java.util.Objects;
import java.util.UUID;

public record UserId(UUID id) {
    public UserId {
        Objects.requireNonNull(id, "User id cannot be null!");
    }

    public UserNotFoundException notFound() {
        return new UserNotFoundException();
    }

    public static UserId create() {
        return new UserId(UUID.randomUUID());
    }
}
