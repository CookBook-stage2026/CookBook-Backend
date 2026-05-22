package be.xplore.cookbook.rest.controller.schedule;

import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwner;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;
import be.xplore.cookbook.core.domain.weekschedule.command.CreateWeekScheduleCommand;
import be.xplore.cookbook.core.domain.weekschedule.command.DayEntry;
import be.xplore.cookbook.core.domain.weekschedule.command.FindWeekSchedulesByOwnerQuery;
import be.xplore.cookbook.core.domain.weekschedule.command.SuggestRecipeForDayQuery;
import be.xplore.cookbook.core.domain.weekschedule.command.SuggestWeekScheduleQuery;
import be.xplore.cookbook.core.service.WeekScheduleService;
import be.xplore.cookbook.rest.dto.schedule.request.CreateDayScheduleDto;
import be.xplore.cookbook.rest.dto.schedule.request.CreateWeekScheduleDto;
import be.xplore.cookbook.rest.dto.schedule.response.WeekScheduleDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/schedules/personal")
public class PersonalWeekScheduleController {

    private final WeekScheduleService scheduleService;

    public PersonalWeekScheduleController(WeekScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public WeekScheduleDto createSchedule(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateWeekScheduleDto dto
    ) {
        UserId userId = getUserIdFromJwt(jwt);
        ScheduleOwner owner = ScheduleOwner.forUser(userId);
        return WeekScheduleDto.fromDomain(scheduleService.saveWeekSchedule(
                new CreateWeekScheduleCommand(dto.weekStartDate(), toDayEntries(dto.days()), owner, userId)
        ));
    }

    @GetMapping
    public List<WeekScheduleDto> listSchedules(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to
    ) {
        UserId userId = getUserIdFromJwt(jwt);
        ScheduleOwner owner = ScheduleOwner.forUser(userId);
        return scheduleService.findSchedulesForOwner(new FindWeekSchedulesByOwnerQuery(owner, from, to, userId))
                .stream()
                .map(WeekScheduleDto::fromDomain)
                .toList();
    }

    @GetMapping("/suggest/day/{date}")
    public WeekScheduleDto suggestRecipeForDate(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable LocalDate date
    ) {
        UserId userId = getUserIdFromJwt(jwt);
        ScheduleOwner owner = ScheduleOwner.forUser(userId);
        WeekSchedule schedule = scheduleService.suggestRecipeForDay(
                new SuggestRecipeForDayQuery(owner, date, userId));
        return WeekScheduleDto.fromDomain(schedule);
    }

    @GetMapping("/suggest/week/{weekStartDate}")
    public WeekScheduleDto suggestWeekSchedule(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable LocalDate weekStartDate
    ) {
        UserId userId = getUserIdFromJwt(jwt);
        ScheduleOwner owner = ScheduleOwner.forUser(userId);
        WeekSchedule schedule = scheduleService.suggestWeekSchedule(
                new SuggestWeekScheduleQuery(owner, weekStartDate, userId));
        return WeekScheduleDto.fromDomain(schedule);
    }

    private UserId getUserIdFromJwt(Jwt jwt) {
        return new UserId(UUID.fromString(jwt.getSubject()));
    }

    private List<DayEntry> toDayEntries(List<CreateDayScheduleDto> days) {
        return days.stream()
                .map(d -> new DayEntry(new RecipeId(d.recipeId()), d.day()))
                .toList();
    }
}
