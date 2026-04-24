package cookbook.stage.backend.api;

import cookbook.stage.backend.api.input.UpdateUserPreferencesRequest;
import cookbook.stage.backend.api.result.UserPreferencesDto;
import cookbook.stage.backend.domain.ingredient.IngredientId;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.domain.user.UserPreferences;
import cookbook.stage.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Gets the preferences of a user
     *
     * @param jwt The JWT token of the authenticated user
     * @return The preferences containing excluded categories and ingredient ids
     */
    @GetMapping("/preferences")
    public UserPreferencesDto getPreferences(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UserPreferences preferences = userService.findPreferences(UserId.fromJwt(jwt));
        return UserPreferencesDto.fromDomain(preferences);
    }

    /**
     * Updates the preferences of a user
     *
     * @param jwt The JWT token of the authenticated user
     * @param request The new preferences containing excluded category and ingredient ids
     */
    @PutMapping("/preferences")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePreferences(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateUserPreferencesRequest request
    ) {
        userService.updatePreferences(
                UserId.fromJwt(jwt),
                request.excludedCategories(),
                request.excludedIngredientIds().stream().map(IngredientId::new).toList()
        );
    }
}
