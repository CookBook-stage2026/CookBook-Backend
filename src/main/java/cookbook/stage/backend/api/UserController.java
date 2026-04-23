package cookbook.stage.backend.api;

import cookbook.stage.backend.api.input.CreateWeekScheduleDto;
import cookbook.stage.backend.api.result.WeekScheduleDto;
import cookbook.stage.backend.domain.recipe.RecipeId;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    /**
     * Creates a new week schedule based on the given DTO.
     * @param dto the new weekSchedule, without an id.
     * @return The newly created week schedule
     */
    @PostMapping("/schedule")
    public WeekScheduleDto createSchedule(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateWeekScheduleDto dto
            ) {
        Map<DayOfWeek, RecipeId> dailyRecipeIds = new EnumMap<>(DayOfWeek.class);

        dto.dailyRecipeIds().forEach((day, recipeId) -> dailyRecipeIds.put(day, new RecipeId(recipeId)));
        var newSchedule = service.saveWeekSchedule(
                dailyRecipeIds,
                new UserId(UUID.fromString(jwt.getSubject())));
        return WeekScheduleDto.fromDomain(newSchedule);
    }
}
