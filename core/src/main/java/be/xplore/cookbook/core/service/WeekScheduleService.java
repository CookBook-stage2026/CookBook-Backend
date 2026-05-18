package be.xplore.cookbook.core.service;

import be.xplore.cookbook.core.common.Paging;
import be.xplore.cookbook.core.domain.exception.NotFoundException;
import be.xplore.cookbook.core.domain.exception.UserNotFoundException;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.recipe.RecipeSummary;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.user.UserPreferences;
import be.xplore.cookbook.core.domain.weekschedule.DaySchedule;
import be.xplore.cookbook.core.domain.weekschedule.DayScheduleId;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;
import be.xplore.cookbook.core.domain.weekschedule.WeekScheduleId;
import be.xplore.cookbook.core.domain.weekschedule.command.CreateWeekScheduleCommand;
import be.xplore.cookbook.core.domain.weekschedule.command.DayEntry;
import be.xplore.cookbook.core.domain.weekschedule.command.FindWeekSchedulesByUserQuery;
import be.xplore.cookbook.core.domain.weekschedule.command.SuggestRecipeForDayQuery;
import be.xplore.cookbook.core.domain.weekschedule.command.UpdateWeekScheduleCommand;
import be.xplore.cookbook.core.port.recipe.ScheduleSuggestionsPort;
import be.xplore.cookbook.core.repository.RecipeRepository;
import be.xplore.cookbook.core.repository.UserPreferenceRepository;
import be.xplore.cookbook.core.repository.UserRepository;
import be.xplore.cookbook.core.repository.WeekScheduleRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WeekScheduleService {

    private static final DayOfWeek FIRST_DAY_OF_WEEK = DayOfWeek.MONDAY;
    private static final int REMAINING_DAYS_IN_WEEK = 6;

    private final WeekScheduleRepository weekScheduleRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final UserPreferenceRepository preferenceRepository;
    private final ScheduleSuggestionsPort aiPort;

    public WeekScheduleService(WeekScheduleRepository weekScheduleRepository,
                               UserRepository userRepository,
                               RecipeRepository recipeRepository,
                               UserPreferenceRepository preferenceRepository,
                               ScheduleSuggestionsPort aiPort) {
        this.weekScheduleRepository = weekScheduleRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
        this.preferenceRepository = preferenceRepository;
        this.aiPort = aiPort;
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

    public void deleteWeekSchedule(WeekScheduleId id, UserId userId) {
        var existing = weekScheduleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Week schedule not found"));

        if (!existing.user().id().equals(userId)) {
            throw new UserNotFoundException();
        }

        weekScheduleRepository.deleteById(id);
    }

    public WeekSchedule suggestRecipeForDay(SuggestRecipeForDayQuery query) {
        User user = userRepository.findById(query.userId())
                .orElseThrow(UserNotFoundException::new);
        LocalDate dayToSuggestFor = query.dayToSuggestFor();

        List<WeekSchedule> schedules = resolveThreeWeekSchedules(user, dayToSuggestFor);
        WeekSchedule targetWeek = schedules.get(1);

        UserPreferences preferences = preferenceRepository.findPreferences(user)
                .orElseThrow(UserNotFoundException::new);
        List<RecipeSummary> availableRecipes = recipeRepository.findAllSummariesWithFilter(
                List.of(), preferences, user, Paging.unpaged()).content();

        RecipeId recipeId = aiPort.suggestRecipeForDay(dayToSuggestFor.getDayOfWeek(), schedules, availableRecipes);
        Recipe recipe = recipeRepository.findById(recipeId, user.id())
                .orElseThrow(recipeId::notFound);

        return targetWeek.assignRecipe(dayToSuggestFor.getDayOfWeek(), recipe);
    }

    private List<WeekSchedule> resolveThreeWeekSchedules(User user, LocalDate targetDate) {
        LocalDate previousWeekStart = targetDate
                .with(TemporalAdjusters.previousOrSame(FIRST_DAY_OF_WEEK))
                .minusWeeks(1);

        LocalDate currentWeekStart = previousWeekStart.plusWeeks(1);
        LocalDate nextWeekStart = previousWeekStart.plusWeeks(2);

        List<WeekSchedule> persisted = weekScheduleRepository.findAllByUserIdAndDateRange(
                user.id(),
                previousWeekStart,
                nextWeekStart.plusDays(REMAINING_DAYS_IN_WEEK)
        );

        Map<LocalDate, WeekSchedule> byStartDate = persisted.stream()
                .collect(Collectors.toMap(WeekSchedule::weekStartDate, Function.identity()));

        WeekSchedule currentWeek = byStartDate.containsKey(currentWeekStart)
                ? byStartDate.get(currentWeekStart)
                : weekScheduleRepository.save(WeekSchedule.empty(user, currentWeekStart));

        return List.of(
                byStartDate.getOrDefault(previousWeekStart, WeekSchedule.empty(user, previousWeekStart)),
                currentWeek,
                byStartDate.getOrDefault(nextWeekStart, WeekSchedule.empty(user, nextWeekStart))
        );
    }
}
