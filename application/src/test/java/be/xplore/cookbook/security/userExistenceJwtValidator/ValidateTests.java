package be.xplore.cookbook.security.userExistenceJwtValidator;

import be.xplore.cookbook.core.domain.exception.UserNotFoundException;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.user.command.FindUserByIdQuery;
import be.xplore.cookbook.core.service.UserService;
import be.xplore.cookbook.security.UserExistenceJwtValidator;
import be.xplore.cookbook.security.exception.OAuth2Exception;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
        when(userService.findById(new FindUserByIdQuery(new UserId(userId)))).thenReturn(
                new User(new UserId(userId), "test@gmail.com", "test", "google", "google"));

        var result = validator.validate(jwt);

        assertThat(result)
                .isNotNull()
                .matches(r -> !r.hasErrors(), "Result should have no errors");

        verify(userService).findById(new FindUserByIdQuery(new UserId(userId)));
    }

    @Test
    void validate_InvalidUuidSubject_ThrowsOAuth2Exception() {
        var jwt = createJwtWithSubject("not-a-valid-uuid");

        assertThatThrownBy(() -> validator.validate(jwt))
                .isInstanceOf(OAuth2Exception.class)
                .hasMessage("Invalid JWT subject");
    }

    @Test
    void validate_ValidUuidButUserNotFound_ReturnsFailure() {
        var userId = UUID.randomUUID();
        var jwt = createJwtWithSubject(userId.toString());
        when(userService.findById(new FindUserByIdQuery(new UserId(userId))))
                .thenThrow(new UserNotFoundException("User not found"));

        var result = validator.validate(jwt);

        assertThat(result.getErrors())
                .isNotEmpty()
                .hasSize(1)
                .first()
                .extracting(OAuth2Error::getErrorCode, OAuth2Error::getDescription)
                .containsExactly("invalid_token", "The user associated with this token does not exist.");
    }

    private Jwt createJwtWithSubject(String subject) {
        return Jwt.withTokenValue("dummy.token.value")
                .header("alg", "none")
                .subject(subject)
                .build();
    }
}
