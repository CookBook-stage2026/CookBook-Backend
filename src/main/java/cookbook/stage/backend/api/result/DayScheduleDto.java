package cookbook.stage.backend.api.result;

import cookbook.stage.backend.domain.week_schedule.DaySchedule;

import java.time.DayOfWeek;
import java.util.UUID;

public record DayScheduleDto(UUID dayScheduleId, RecipeSummaryDto recipeSummary, DayOfWeek day) {
    public static DayScheduleDto fromDomain(DaySchedule daySchedule) {
        return new DayScheduleDto(daySchedule.id().id(),
                RecipeSummaryDto.fromDomain(daySchedule.recipe().summarize()), daySchedule.day());
    }
}
