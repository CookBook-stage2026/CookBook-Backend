package be.xplore.cookbook.ai.dto;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

public record SuggestedWeekScheduleIds(List<DayRecipeEntry> days) {
    public record DayRecipeEntry(DayOfWeek day, UUID recipeId) {
    }
}
