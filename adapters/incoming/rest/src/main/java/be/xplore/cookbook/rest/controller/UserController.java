package be.xplore.cookbook.rest.controller;

import be.xplore.cookbook.rest.dto.request.UpdateUserPreferencesRequest;
import be.xplore.cookbook.rest.dto.response.UserPreferencesDto;
import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.user.UserPreferences;
import be.xplore.cookbook.core.service.UserPreferenceService;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserPreferenceService userPreferenceService;

    public UserController(UserPreferenceService userPreferenceService) {
        this.userPreferenceService = userPreferenceService;
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
        UserPreferences preferences = userPreferenceService.findPreferences(UserId.fromJwt(jwt));
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
        userPreferenceService.updatePreferences(
                UserId.fromJwt(jwt),
                request.excludedCategories(),
                request.excludedIngredientIds().stream().map(IngredientId::new).toList()
        );
    }

    private UserId fromJwt(Jwt jwt) {
        return new UserId(UUID.fromString(jwt.getSubject()));
    }
}
