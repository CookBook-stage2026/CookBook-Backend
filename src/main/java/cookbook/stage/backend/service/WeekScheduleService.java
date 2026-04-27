package cookbook.stage.backend.service;

import cookbook.stage.backend.domain.recipe.RecipeId;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.domain.weekschedule.DaySchedule;
import cookbook.stage.backend.domain.weekschedule.DayScheduleId;
import cookbook.stage.backend.domain.weekschedule.WeekSchedule;
import cookbook.stage.backend.domain.weekschedule.WeekScheduleId;
import cookbook.stage.backend.domain.weekschedule.WeekScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

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

        List<DaySchedule> dailyRecipes = IntStream.range(0, recipeIds.size())
                .mapToObj(i -> {
                    RecipeId recipeId = new RecipeId(recipeIds.get(i));
                    DayOfWeek day = days.get(i);
                    var recipe = recipeService.findById(recipeId, userId);
                    return new DaySchedule(DayScheduleId.create(), recipe, day);
                })
                .toList();

        WeekSchedule weekSchedule = new WeekSchedule(WeekScheduleId.create(), user, dailyRecipes);
        return repo.save(weekSchedule);
    }
}
