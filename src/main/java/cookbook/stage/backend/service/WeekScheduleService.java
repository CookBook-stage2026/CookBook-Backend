package cookbook.stage.backend.service;

import cookbook.stage.backend.domain.recipe.RecipeId;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.domain.week_schedule.DaySchedule;
import cookbook.stage.backend.domain.week_schedule.DayScheduleId;
import cookbook.stage.backend.domain.week_schedule.WeekSchedule;
import cookbook.stage.backend.domain.week_schedule.WeekScheduleId;
import cookbook.stage.backend.domain.week_schedule.WeekScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@Service
public class WeekScheduleService {
    private final WeekScheduleRepository repo;
    private final UserService userService;
    private final RecipeService recipeService;

    public WeekScheduleService(WeekScheduleRepository repo, UserService userService, RecipeService recipeService) {
        this.repo = repo;
        this.userService = userService;
        this.recipeService = recipeService;
    }

    public WeekSchedule saveWeekSchedule(List<UUID> recipeIds, List<DayOfWeek> days, UserId userId) {
        var user = userService.findById(userId)
                .orElseThrow(userId::notFound);

        List<DaySchedule> dailyRecipes = new java.util.ArrayList<>();
        for (int i = 0; i < recipeIds.size(); i++) {
            RecipeId recipeId = new RecipeId(recipeIds.get(i));
            DayOfWeek day = days.get(i);

            var recipe = recipeService.findById(recipeId, userId);
            dailyRecipes.add(new DaySchedule(DayScheduleId.create(), recipe, day));
        }

        WeekSchedule weekSchedule = new WeekSchedule(WeekScheduleId.create(), user, dailyRecipes);
        return repo.save(weekSchedule);
    }
}
