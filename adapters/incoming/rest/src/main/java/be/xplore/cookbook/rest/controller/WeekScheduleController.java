package be.xplore.cookbook.rest.controller;

import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;
import be.xplore.cookbook.core.domain.weekschedule.WeekScheduleId;
import be.xplore.cookbook.core.domain.weekschedule.command.CreateWeekScheduleCommand;
import be.xplore.cookbook.core.domain.weekschedule.command.DayEntry;
import be.xplore.cookbook.core.domain.weekschedule.command.FindWeekSchedulesByUserQuery;
import be.xplore.cookbook.core.domain.weekschedule.command.SuggestRecipeForDayQuery;
import be.xplore.cookbook.core.domain.weekschedule.command.UpdateWeekScheduleCommand;
import be.xplore.cookbook.core.service.WeekScheduleService;
import be.xplore.cookbook.rest.dto.request.CreateWeekScheduleDto;
import be.xplore.cookbook.rest.dto.request.UpdateWeekScheduleDto;
import be.xplore.cookbook.rest.dto.response.WeekScheduleDto;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
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

        List<DayEntry> days = dto.days().stream()
                .map(d -> new DayEntry(
                        new RecipeId(d.recipeId()), d.day()))
                .toList();

        var command = new CreateWeekScheduleCommand(dto.weekStartDate(), days, userId);
        return WeekScheduleDto.fromDomain(service.saveWeekSchedule(command));
    }

    @PutMapping("/{id}")
    @Transactional
    public void updateSchedule(@AuthenticationPrincipal Jwt jwt,
                               @PathVariable UUID id,
                               @Valid @RequestBody UpdateWeekScheduleDto dto) {
        UserId userId = getUserIdFromJwt(jwt);

        List<DayEntry> days = dto.days().stream()
                .map(d -> new DayEntry(
                        new RecipeId(d.recipeId()), d.day()))
                .toList();

        var command = new UpdateWeekScheduleCommand(
                new WeekScheduleId(id), userId, days);
        service.updateWeekSchedule(command);
    }

    @GetMapping
    public List<WeekScheduleDto> listSchedules(@AuthenticationPrincipal Jwt jwt,
                                               @RequestParam(required = false)
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                               @RequestParam(required = false)
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        UserId userId = getUserIdFromJwt(jwt);
        FindWeekSchedulesByUserQuery query = new FindWeekSchedulesByUserQuery(userId, from, to);
        return service.findSchedulesForUser(query).stream()
                .map(WeekScheduleDto::fromDomain)
                .toList();
    }

    @GetMapping("/suggest/{date}")
    public WeekScheduleDto suggestRecipeForDate(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable LocalDate date
    ) {
        WeekSchedule schedule = service.suggestRecipeForDay(new SuggestRecipeForDayQuery(date, getUserIdFromJwt(jwt)));

        return WeekScheduleDto.fromDomain(schedule);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSchedule(@AuthenticationPrincipal Jwt jwt,
                               @PathVariable UUID id) {
        UserId userId = getUserIdFromJwt(jwt);
        service.deleteWeekSchedule(new WeekScheduleId(id), userId);
    }

    private UserId getUserIdFromJwt(Jwt jwt) {
        return new UserId(UUID.fromString(jwt.getSubject()));
    }
}
