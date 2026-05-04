package be.xplore.cookbook.security.userExistenceJwtValidator;

import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.user.command.FindUserByIdQuery;
import be.xplore.cookbook.core.service.UserService;
import be.xplore.cookbook.security.UserExistenceJwtValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ValidateTests {

    private UserService userService;
    private UserExistenceJwtValidator validator;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        validator = new UserExistenceJwtValidator(userService);
    }

    @Test
    void validate_ValidUuidAndUserExists_ReturnsSuccess() {
        var userId = UUID.randomUUID();
        var jwt = createJwtWithSubject(userId.toString());
        when(userService.findById(new FindUserByIdQuery(new UserId(userId)))).thenReturn(Optional.of(
                new User(new UserId(userId), "test@gmail.com", "test", "google", "google")));

        var result = validator.validate(jwt);

        assertThat(result)
                .isNotNull()
                .matches(r -> !r.hasErrors(), "Result should have no errors");

        verify(userService).findById(new FindUserByIdQuery(new UserId(userId)));
    }

    @Test
    void validate_ValidUuidButUserDoesNotExist_ReturnsFailure() {
        var userId = UUID.randomUUID();
        var jwt = createJwtWithSubject(userId.toString());
        when(userService.findById(new FindUserByIdQuery(new UserId(userId)))).thenReturn(Optional.empty());

        var result = validator.validate(jwt);

        assertThat(result.getErrors())
                .isNotEmpty()
                .hasSize(1)
                .first()
                .extracting(OAuth2Error::getErrorCode, OAuth2Error::getDescription)
                .containsExactly("invalid_token", "The user associated with this token does not exist.");

        assertNull(verify(userService).findById(new FindUserByIdQuery(new UserId(userId))));
    }

    private Jwt createJwtWithSubject(String subject) {
        return Jwt.withTokenValue("dummy.token.value")
                .header("alg", "none")
                .subject(subject)
                .build();
    }
}
