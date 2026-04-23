package cookbook.stage.backend.api.input;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.UUID;

public record CreateWeekScheduleDto(
        Map<DayOfWeek, UUID> dailyRecipeIds
) {
}
