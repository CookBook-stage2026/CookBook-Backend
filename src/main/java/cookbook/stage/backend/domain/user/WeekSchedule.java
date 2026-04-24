package cookbook.stage.backend.domain.user;

import cookbook.stage.backend.domain.recipe.Recipe;
import java.time.DayOfWeek;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public record WeekSchedule(WeekScheduleId id, Map<DayOfWeek, Recipe> dailyRecipes) {
    public WeekSchedule(WeekScheduleId id, Map<DayOfWeek, Recipe> dailyRecipes) {
        if (id == null) {
            throw new IllegalArgumentException("WeekSchedule must have an id");
        }
        this.id = id;
        this.dailyRecipes = new EnumMap<>(DayOfWeek.class);
        this.dailyRecipes.putAll(dailyRecipes);
    }

    @Override
    public Map<DayOfWeek, Recipe> dailyRecipes() {
        return Collections.unmodifiableMap(dailyRecipes);
    }
}
