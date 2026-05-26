package be.xplore.cookbook.rest.dto.schedule.response;

import be.xplore.cookbook.core.domain.weekschedule.DaySchedule;
import be.xplore.cookbook.rest.dto.recipe.response.RecipeSummaryDto;

import java.time.DayOfWeek;
import java.util.UUID;

public record DayScheduleDto(
        UUID dayScheduleId,
        RecipeSummaryDto recipeSummary,
        DayOfWeek day
) {
    public static DayScheduleDto fromDomain(DaySchedule daySchedule) {
        return new DayScheduleDto(daySchedule.id().id(),
                RecipeSummaryDto.fromDomain(daySchedule.recipe().summarize()), daySchedule.day());
    }
}
