package be.xplore.cookbook.controller;

import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.DaySchedule;
import be.xplore.cookbook.core.domain.weekschedule.DayScheduleId;
import be.xplore.cookbook.core.service.RecipeService;
import be.xplore.cookbook.core.service.WeekScheduleService;
import be.xplore.cookbook.dto.request.CreateWeekScheduleDto;
import be.xplore.cookbook.dto.response.WeekScheduleDto;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/schedules")
public class WeekScheduleController {
    private final WeekScheduleService service;
    private final RecipeService recipeService;

    public WeekScheduleController(WeekScheduleService service, RecipeService recipeService) {
        this.service = service;
        this.recipeService = recipeService;
    }

    /**
     * Creates a new week schedule based on the given DTO.
     *
     * @param dto the new weekSchedule, without an id.
     * @return The newly created week schedule
     */
    @PostMapping
    public WeekScheduleDto createSchedule(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateWeekScheduleDto dto
    ) {
        UserId userId = new UserId(UUID.fromString(jwt.getSubject()));

        var daySchedules = dto.days().stream()
                .map(dayDto -> {
                    var recipe = recipeService.findById(new RecipeId(dayDto.recipeId()), userId);
                    return new DaySchedule(DayScheduleId.create(), recipe, dayDto.day());
                })
                .toList();

        var newSchedule = service.saveWeekSchedule(daySchedules, userId);

        return WeekScheduleDto.fromDomain(newSchedule);
    }

    /**
     * Gets the week schedule for the logged-in user
     * @return the week schedule for the logged-in user
     */
    @GetMapping("/user")
    public WeekScheduleDto findForUser(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return WeekScheduleDto.fromDomain(service.findByUserId(new UserId(UUID.fromString(jwt.getSubject()))));
    }
}
