package cookbook.stage.backend.api;

import cookbook.stage.backend.api.input.UpdateUserPreferencesRequest;
import cookbook.stage.backend.domain.ingredient.IngredientId;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.domain.user.UserPreferences;
import cookbook.stage.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Updates the preferences of a user
     *
     * @param id      The id of the user to update preferences for
     * @param request The new preferences containing excluded category and ingredient ids
     */
    @PutMapping("/{id}/preferences")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePreferences(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserPreferencesRequest request
    ) {
        userService.updatePreferences(
                new UserId(id),
                new UserPreferences(
                        request.excludedCategories(),
                        request.excludedIngredientIds().stream().map(IngredientId::new).toList()
                )
        );
    }
}
