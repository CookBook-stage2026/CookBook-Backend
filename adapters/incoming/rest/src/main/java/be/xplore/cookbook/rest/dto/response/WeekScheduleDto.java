package be.xplore.cookbook.rest.dto.response;

import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;

import java.util.List;
import java.util.UUID;

public record WeekScheduleDto(
        UUID id,
        List<DayScheduleDto> days
) {
    public static WeekScheduleDto fromDomain(WeekSchedule schedule) {
        return new WeekScheduleDto(schedule.id().id(),
                schedule.dailyRecipes().stream().map(DayScheduleDto::fromDomain).toList());
    }
}
