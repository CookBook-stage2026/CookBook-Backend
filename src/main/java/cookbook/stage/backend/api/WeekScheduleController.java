package cookbook.stage.backend.api;

import cookbook.stage.backend.api.input.CreateDayScheduleDto;
import cookbook.stage.backend.api.input.CreateWeekScheduleDto;
import cookbook.stage.backend.api.result.WeekScheduleDto;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.service.WeekScheduleService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/schedules")
public class WeekScheduleController {
    private final WeekScheduleService service;

    public WeekScheduleController(WeekScheduleService service) {
        this.service = service;
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
        var newSchedule = service.saveWeekSchedule(dto.days().stream().map(CreateDayScheduleDto::recipeId).toList(),
                dto.days().stream().map(CreateDayScheduleDto::day).toList(),
                new UserId(UUID.fromString(jwt.getSubject())));

        return WeekScheduleDto.fromDomain(newSchedule);
    }
}
