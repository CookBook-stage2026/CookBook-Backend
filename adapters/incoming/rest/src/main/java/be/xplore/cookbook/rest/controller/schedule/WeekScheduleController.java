package be.xplore.cookbook.rest.controller.schedule;

import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.WeekScheduleId;
import be.xplore.cookbook.core.domain.weekschedule.command.DayEntry;
import be.xplore.cookbook.core.domain.weekschedule.command.DeleteWeekScheduleCommand;
import be.xplore.cookbook.core.domain.weekschedule.command.UpdateWeekScheduleCommand;
import be.xplore.cookbook.core.service.WeekScheduleService;
import be.xplore.cookbook.rest.dto.schedule.request.UpdateWeekScheduleDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/schedules")
public class WeekScheduleController {

    private final WeekScheduleService scheduleService;

    public WeekScheduleController(WeekScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PutMapping("/{id}")
    @Transactional
    public void updateSchedule(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateWeekScheduleDto dto
    ) {
        UserId userId = getUserIdFromJwt(jwt);

        List<DayEntry> days = dto.days().stream()
                .map(d -> new DayEntry(
                        new RecipeId(d.recipeId()), d.day()))
                .toList();

        scheduleService.updateWeekSchedule(
                new UpdateWeekScheduleCommand(new WeekScheduleId(id), userId, days)
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSchedule(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id
    ) {
        scheduleService.deleteWeekSchedule(
                new DeleteWeekScheduleCommand(new WeekScheduleId(id), getUserIdFromJwt(jwt)));
    }

    private UserId getUserIdFromJwt(Jwt jwt) {
        return new UserId(UUID.fromString(jwt.getSubject()));
    }
}
