package be.xplore.cookbook.rest.controller.user;

import be.xplore.cookbook.core.domain.ingredient.IngredientId;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.user.command.FindUserByIdQuery;
import be.xplore.cookbook.core.domain.user.command.FindUserPreferencesQuery;
import be.xplore.cookbook.core.domain.user.command.UpdateUserPreferencesCommand;
import be.xplore.cookbook.core.service.UserPreferenceService;
import be.xplore.cookbook.core.service.UserService;
import be.xplore.cookbook.rest.dto.user.request.UpdateUserPreferencesRequest;
import be.xplore.cookbook.rest.dto.user.response.UserDto;
import be.xplore.cookbook.rest.dto.user.response.UserPreferencesDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserPreferenceService userPreferenceService;
    private final UserService userService;

    public UserController(UserPreferenceService userPreferenceService, UserService userService) {
        this.userPreferenceService = userPreferenceService;
        this.userService = userService;
    }

    @GetMapping("/preferences")
    public UserPreferencesDto getPreferences(@AuthenticationPrincipal Jwt jwt) {
        return UserPreferencesDto.fromDomain(
                userPreferenceService.findPreferences(new FindUserPreferencesQuery(getUserIdFromJwt(jwt)))
        );
    }

    @PutMapping("/preferences")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePreferences(@AuthenticationPrincipal Jwt jwt,
                                  @Valid @RequestBody UpdateUserPreferencesRequest request) {
        List<IngredientId> excludedIngredientIds = request.excludedIngredientIds().stream()
                .map(IngredientId::new)
                .toList();

        userPreferenceService.updatePreferences(new UpdateUserPreferencesCommand(
                getUserIdFromJwt(jwt), request.excludedCategories(), excludedIngredientIds
        ));
    }

    @GetMapping("/me")
    public UserDto getCurrentUser(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return UserDto.fromDomain(
                userService.findById(new FindUserByIdQuery(getUserIdFromJwt(jwt)))
        );
    }

    private UserId getUserIdFromJwt(Jwt jwt) {
        return new UserId(UUID.fromString(jwt.getSubject()));
    }
}
