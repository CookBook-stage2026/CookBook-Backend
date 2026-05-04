package be.xplore.cookbook.rest.controller;

import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.command.CreateWeekScheduleCommand;
import be.xplore.cookbook.core.domain.weekschedule.command.FindWeekScheduleByUserQuery;
import be.xplore.cookbook.core.service.WeekScheduleService;
import be.xplore.cookbook.rest.dto.request.CreateWeekScheduleDto;
import be.xplore.cookbook.rest.dto.response.WeekScheduleDto;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/schedules")
public class WeekScheduleController {
    private final WeekScheduleService service;

    public WeekScheduleController(WeekScheduleService service) {
        this.service = service;
    }

    @PostMapping
    @Transactional
    public WeekScheduleDto createSchedule(@AuthenticationPrincipal Jwt jwt,
                                          @Valid @RequestBody CreateWeekScheduleDto dto) {
        UserId userId = getUserIdFromJwt(jwt);

        List<CreateWeekScheduleCommand.DayEntry> days = dto.days().stream()
                .map(d -> new CreateWeekScheduleCommand.DayEntry(new RecipeId(d.recipeId()), d.day()))
                .toList();

        return WeekScheduleDto.fromDomain(service.saveWeekSchedule(new CreateWeekScheduleCommand(days, userId)));
    }

    @GetMapping
    public WeekScheduleDto findForUser(@AuthenticationPrincipal Jwt jwt) {
        return WeekScheduleDto.fromDomain(service.findByUserId(new FindWeekScheduleByUserQuery(getUserIdFromJwt(jwt))));
    }

    private UserId getUserIdFromJwt(Jwt jwt) {
        return new UserId(UUID.fromString(jwt.getSubject()));
    }
}
