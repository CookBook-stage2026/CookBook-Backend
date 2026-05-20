package be.xplore.cookbook.rest.dto.response;

import be.xplore.cookbook.core.domain.user.User;

import java.util.UUID;

public record UserDto(UUID userId, String email, String displayName) {
    public static UserDto fromDomain(User user) {
        return new UserDto(
                user.id().id(),
                user.email(),
                user.displayName()
        );
    }
}
