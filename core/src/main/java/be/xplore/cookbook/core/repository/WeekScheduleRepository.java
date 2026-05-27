package be.xplore.cookbook.core.repository;

import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwner;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwnerType;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;
import be.xplore.cookbook.core.domain.weekschedule.WeekScheduleId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeekScheduleRepository {
    WeekSchedule save(WeekSchedule schedule);

    List<WeekSchedule> findAllByOwner(ScheduleOwner owner);

    List<WeekSchedule> findAllByOwnerAndDateRange(ScheduleOwner owner, LocalDate from, LocalDate to);

    Optional<WeekSchedule> findById(WeekScheduleId id);

    Optional<WeekSchedule> findByOwnerAndWeekStartDate(ScheduleOwner owner, LocalDate weekStartDate);

    void delete(WeekSchedule schedule);

    void deleteAllScheduledRecipesFromUser(ScheduleOwner owner, UserId userId);

    void deleteByRecipeIdAndOwnerType(RecipeId recipeId, ScheduleOwnerType ownerType);
}
