package be.xplore.cookbook.core.service;

import be.xplore.cookbook.core.domain.exception.NotFoundException;
import be.xplore.cookbook.core.domain.exception.UserNotFoundException;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.DaySchedule;
import be.xplore.cookbook.core.domain.weekschedule.DayScheduleId;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;
import be.xplore.cookbook.core.domain.weekschedule.WeekScheduleId;
import be.xplore.cookbook.core.domain.weekschedule.command.CreateWeekScheduleCommand;
import be.xplore.cookbook.core.domain.weekschedule.command.DayEntry;
import be.xplore.cookbook.core.domain.weekschedule.command.FindWeekSchedulesByUserQuery;
import be.xplore.cookbook.core.domain.weekschedule.command.UpdateWeekScheduleCommand;
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

        List<DaySchedule> daySchedules = extractDaySchedulesFromDayEntries(command.days(), command.userId());

        weekScheduleRepository.findByUserIdAndWeekStartDate(command.userId(), command.weekStartDate())
                .ifPresent(existing -> weekScheduleRepository.deleteById(existing.id()));

        return weekScheduleRepository.save(
                new WeekSchedule(WeekScheduleId.create(), user, command.weekStartDate(), daySchedules));
    }

    public WeekSchedule updateWeekSchedule(UpdateWeekScheduleCommand command) {
        var existingSchedule = weekScheduleRepository.findById(command.weekScheduleId())
                .orElseThrow(() -> new NotFoundException("Week schedule not found"));

        if (!existingSchedule.user().id().equals(command.userId())) {
            throw new NotFoundException("Week schedule not found");
        }

        List<DaySchedule> daySchedules = extractDaySchedulesFromDayEntries(command.days(), command.userId());

        var updatedSchedule = new WeekSchedule(
                existingSchedule.id(),
                existingSchedule.user(),
                existingSchedule.weekStartDate(),
                daySchedules
        );

        return weekScheduleRepository.save(updatedSchedule);
    }

    public List<WeekSchedule> findSchedulesForUser(FindWeekSchedulesByUserQuery query) {
        if (query.hasDateRange()) {
            return weekScheduleRepository.findAllByUserIdAndDateRange(
                    query.userId(), query.from(), query.to());
        }
        return weekScheduleRepository.findAllByUserId(query.userId());
    }

    private List<DaySchedule> extractDaySchedulesFromDayEntries(List<DayEntry> dayEntries, UserId userId) {
        return dayEntries.stream()
                .map(entry -> {
                    Recipe recipe = recipeRepository.findById(entry.recipeId(), userId)
                            .orElseThrow(entry.recipeId()::notFound);
                    return new DaySchedule(DayScheduleId.create(), recipe, entry.day());
                })
                .toList();
    }
}
