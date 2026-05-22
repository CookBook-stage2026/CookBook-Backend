package be.xplore.cookbook.core.service;

import be.xplore.cookbook.core.domain.exception.NotFoundException;
import be.xplore.cookbook.core.domain.exception.UserNotFoundException;
import be.xplore.cookbook.core.domain.household.HouseholdId;
import be.xplore.cookbook.core.domain.recipe.Recipe;
import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.recipe.RecipeSummary;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.user.UserPreferences;
import be.xplore.cookbook.core.domain.weekschedule.DaySchedule;
import be.xplore.cookbook.core.domain.weekschedule.DayScheduleId;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwner;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;
import be.xplore.cookbook.core.domain.weekschedule.WeekScheduleId;
import be.xplore.cookbook.core.domain.weekschedule.command.CreateWeekScheduleCommand;
import be.xplore.cookbook.core.domain.weekschedule.command.DayEntry;
import be.xplore.cookbook.core.domain.weekschedule.command.DeleteWeekScheduleCommand;
import be.xplore.cookbook.core.domain.weekschedule.command.FindWeekSchedulesByOwnerQuery;
import be.xplore.cookbook.core.domain.weekschedule.command.SuggestRecipeForDayQuery;
import be.xplore.cookbook.core.domain.weekschedule.command.SuggestWeekScheduleQuery;
import be.xplore.cookbook.core.domain.weekschedule.command.UpdateWeekScheduleCommand;
import be.xplore.cookbook.core.port.weekschedule.ScheduleSuggestionsPort;
import be.xplore.cookbook.core.port.weekschedule.SuggestedDayRecipe;
import be.xplore.cookbook.core.repository.HouseholdRepository;
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
    private final HouseholdRepository householdRepository;
    private final ScheduleSuggestionsPort aiPort;

    public WeekScheduleService(WeekScheduleRepository weekScheduleRepository,
                               UserRepository userRepository,
                               RecipeRepository recipeRepository,
                               UserPreferenceRepository preferenceRepository,
                               HouseholdRepository householdRepository,
                               ScheduleSuggestionsPort aiPort) {
        this.weekScheduleRepository = weekScheduleRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
        this.preferenceRepository = preferenceRepository;
        this.householdRepository = householdRepository;
        this.aiPort = aiPort;
    }

    public WeekSchedule saveWeekSchedule(CreateWeekScheduleCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(UserNotFoundException::new);

        List<DaySchedule> daySchedules = extractDaySchedulesFromDayEntries(command.days(), user);

        weekScheduleRepository.findByOwnerAndWeekStartDate(command.owner(), command.weekStartDate())
                .ifPresent(weekScheduleRepository::delete);

        return weekScheduleRepository.save(
                new WeekSchedule(WeekScheduleId.create(), command.owner(), command.weekStartDate(), daySchedules)
        );
    }

    public List<WeekSchedule> findSchedulesForOwner(FindWeekSchedulesByOwnerQuery query) {
        if (query.hasDateRange()) {
            return weekScheduleRepository.findAllByOwnerAndDateRange(
                    query.owner(), query.from(), query.to());
        }
        return weekScheduleRepository.findAllByOwner(query.owner());
    }

    public void updateWeekSchedule(UpdateWeekScheduleCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(UserNotFoundException::new);

        WeekSchedule existingSchedule = weekScheduleRepository.findById(command.weekScheduleId())
                .orElseThrow(() -> new NotFoundException("Week schedule not found"));

        assertUserCanModifySchedule(existingSchedule, command.userId());

        List<DaySchedule> daySchedules = extractDaySchedulesFromDayEntries(command.days(), user);

        weekScheduleRepository.save(new WeekSchedule(
                existingSchedule.id(),
                existingSchedule.owner(),
                existingSchedule.weekStartDate(),
                daySchedules
        ));
    }

    public void deleteWeekSchedule(DeleteWeekScheduleCommand command) {
        WeekSchedule existing = weekScheduleRepository.findById(command.scheduleId())
                .orElseThrow(() -> new NotFoundException("Week schedule not found"));

        assertUserCanModifySchedule(existing, command.userId());

        weekScheduleRepository.delete(existing);
    }

    public WeekSchedule suggestRecipeForDay(SuggestRecipeForDayQuery query) {
        LocalDate dayToSuggestFor = query.dayToSuggestFor();
        User user = userRepository.findById(query.userId())
                .orElseThrow(UserNotFoundException::new);

        List<WeekSchedule> schedules = resolveThreeWeekSchedules(query.owner(), dayToSuggestFor);
        WeekSchedule targetWeek = schedules.get(1);

        assertUserCanModifySchedule(targetWeek, query.userId());

        List<RecipeSummary> availableRecipes = loadAvailableRecipes(query.owner(), user);

        RecipeId recipeId = aiPort.suggestRecipeForDay(dayToSuggestFor.getDayOfWeek(), schedules, availableRecipes);
        Recipe recipe = recipeRepository.findById(recipeId, user)
                .orElseThrow(recipeId::notFound);

        return targetWeek.assignRecipe(dayToSuggestFor.getDayOfWeek(), recipe);
    }

    public WeekSchedule suggestWeekSchedule(SuggestWeekScheduleQuery query) {
        if (query.weekStartDate().getDayOfWeek() != FIRST_DAY_OF_WEEK) {
            throw new IllegalArgumentException("Week start date must be a Monday");
        }

        User user = userRepository.findById(query.userId())
                .orElseThrow(UserNotFoundException::new);

        List<WeekSchedule> schedules = resolveThreeWeekSchedules(query.owner(), query.weekStartDate());
        WeekSchedule targetWeek = schedules.get(1);

        assertUserCanModifySchedule(targetWeek, query.userId());

        List<RecipeSummary> availableRecipes = loadAvailableRecipes(query.owner(), user);

        List<SuggestedDayRecipe> suggestions = aiPort.suggestWeekSchedule(
                query.weekStartDate(), schedules, availableRecipes);

        for (SuggestedDayRecipe suggestion : suggestions) {
            Recipe recipe = recipeRepository.findById(suggestion.recipeId(), user)
                    .orElseThrow(suggestion.recipeId()::notFound);
            targetWeek = targetWeek.assignRecipe(suggestion.day(), recipe);
        }

        return targetWeek;
    }

    private List<DaySchedule> extractDaySchedulesFromDayEntries(List<DayEntry> dayEntries, User user) {
        return dayEntries.stream()
                .map(entry -> {
                    Recipe recipe = recipeRepository.findById(entry.recipeId(), user)
                            .orElseThrow(entry.recipeId()::notFound);
                    return new DaySchedule(DayScheduleId.create(), recipe, entry.day());
                })
                .toList();
    }

    private List<WeekSchedule> resolveThreeWeekSchedules(ScheduleOwner owner, LocalDate targetDate) {
        LocalDate previousWeekStart = targetDate
                .with(TemporalAdjusters.previousOrSame(FIRST_DAY_OF_WEEK))
                .minusWeeks(1);

        LocalDate currentWeekStart = previousWeekStart.plusWeeks(1);
        LocalDate nextWeekStart = previousWeekStart.plusWeeks(2);

        List<WeekSchedule> persisted = weekScheduleRepository.findAllByOwnerAndDateRange(
                owner,
                previousWeekStart,
                nextWeekStart.plusDays(REMAINING_DAYS_IN_WEEK)
        );

        Map<LocalDate, WeekSchedule> byStartDate = persisted.stream()
                .collect(Collectors.toMap(WeekSchedule::weekStartDate, Function.identity()));

        WeekSchedule currentWeek = byStartDate.containsKey(currentWeekStart)
                ? byStartDate.get(currentWeekStart)
                : weekScheduleRepository.save(WeekSchedule.empty(owner, currentWeekStart));

        return List.of(
                byStartDate.getOrDefault(previousWeekStart, WeekSchedule.empty(owner, previousWeekStart)),
                currentWeek,
                byStartDate.getOrDefault(nextWeekStart, WeekSchedule.empty(owner, nextWeekStart))
        );
    }

    private void assertUserCanModifySchedule(WeekSchedule schedule, UserId userId) {
        boolean canModify = switch (schedule.owner().ownerType()) {
            case PERSONAL -> schedule.owner().ownerId().equals(userId.id());
            case HOUSEHOLD -> {
                HouseholdId householdId = new HouseholdId(schedule.owner().ownerId());
                yield householdRepository.isMemberOrCreator(householdId, userId);
            }
        };

        if (!canModify) {
            throw new NotFoundException("Week schedule not found");
        }
    }

    private List<RecipeSummary> loadAvailableRecipes(ScheduleOwner owner, User user) {
        if (owner.isPersonal()) {
            UserPreferences preferences = preferenceRepository.findPreferences(user)
                    .orElseThrow(UserNotFoundException::new);

            return recipeRepository.findAllPersonalSummariesByUserAndPreferences(preferences, user);
        }

        List<UserId> memberIds = householdRepository.findById(new HouseholdId(owner.ownerId()))
                .orElseThrow(() -> new NotFoundException("Household not found"))
                .members()
                .stream()
                .map(User::id)
                .toList();
        return recipeRepository.findAllSummariesByUserIds(memberIds);
    }
}
