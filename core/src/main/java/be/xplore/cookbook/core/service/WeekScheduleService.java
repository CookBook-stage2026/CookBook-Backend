package be.xplore.cookbook.core.service;

import be.xplore.cookbook.core.domain.exception.UserNotFoundException;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.weekschedule.DaySchedule;
import be.xplore.cookbook.core.domain.weekschedule.DayScheduleId;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;
import be.xplore.cookbook.core.domain.weekschedule.WeekScheduleId;
import be.xplore.cookbook.core.domain.weekschedule.command.CreateWeekScheduleCommand;
import be.xplore.cookbook.core.domain.weekschedule.command.FindWeekScheduleByUserQuery;
import be.xplore.cookbook.core.repository.RecipeRepository;
import be.xplore.cookbook.core.repository.UserRepository;
import be.xplore.cookbook.core.repository.WeekScheduleRepository;

import java.util.List;

public class WeekScheduleService {
    private final WeekScheduleRepository weekScheduleRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    public WeekScheduleService(WeekScheduleRepository weekScheduleRepository,
                               UserRepository userRepository,
                               RecipeRepository recipeRepository) {
        this.weekScheduleRepository = weekScheduleRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
    }

    public WeekSchedule saveWeekSchedule(CreateWeekScheduleCommand command) {
        var user = userRepository.findById(command.userId())
                .orElseThrow(UserNotFoundException::new);

        List<DaySchedule> daySchedules = command.days().stream()
                .map(entry -> {
                    Recipe recipe = recipeRepository.findById(entry.recipeId(), command.userId())
                            .orElseThrow(entry.recipeId()::notFound);
                    return new DaySchedule(DayScheduleId.create(), recipe, entry.day());
                })
                .toList();

        return weekScheduleRepository.save(new WeekSchedule(WeekScheduleId.create(), user, daySchedules));
    }

    public WeekSchedule findByUserId(FindWeekScheduleByUserQuery query) {
        return weekScheduleRepository.findForUser(query.userId());
    }
}
