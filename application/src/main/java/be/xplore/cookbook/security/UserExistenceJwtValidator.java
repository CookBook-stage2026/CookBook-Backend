package be.xplore.cookbook.security;

import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.service.UserService;
import be.xplore.cookbook.security.exception.OAuth2Exception;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserExistenceJwtValidator implements OAuth2TokenValidator<Jwt> {

    private final UserService userService;

    public UserExistenceJwtValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        String subject = jwt.getSubject();

        try {
            UUID userId = UUID.fromString(subject);

            if (userService.findById(new UserId(userId)).isPresent()) {
                return OAuth2TokenValidatorResult.success();
            }
        } catch (IllegalArgumentException _) {
            throw new OAuth2Exception("Invalid JWT subject");
        }

        OAuth2Error error = new OAuth2Error(
                "invalid_token",
                "The user associated with this token does not exist.",
                null
        );
        return OAuth2TokenValidatorResult.failure(error);
    }
}
