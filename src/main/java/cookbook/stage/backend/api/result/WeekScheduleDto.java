package cookbook.stage.backend.api.result;

import cookbook.stage.backend.domain.user.WeekSchedule;

import java.time.DayOfWeek;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public record WeekScheduleDto(UUID id,
                               Map<DayOfWeek, RecipeSummaryDto> dailyRecipes) {
    public static WeekScheduleDto fromDomain(WeekSchedule schedule) {
        Map<DayOfWeek, RecipeSummaryDto> dailyRecipes = new EnumMap<>(DayOfWeek.class);

        schedule.dailyRecipes().forEach((day, recipe) ->
                dailyRecipes.put(day, RecipeSummaryDto.fromDomain(recipe.summarize())));
        return new WeekScheduleDto(schedule.id().id(), dailyRecipes);
    }
}
