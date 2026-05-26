package be.xplore.cookbook.jpa.repository.weekschedule;

import be.xplore.cookbook.core.domain.recipe.RecipeId;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwner;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwnerType;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;
import be.xplore.cookbook.core.domain.weekschedule.WeekScheduleId;
import be.xplore.cookbook.core.repository.WeekScheduleRepository;
import be.xplore.cookbook.jpa.repository.weekschedule.entity.JpaWeekScheduleEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class WeekScheduleRepositoryImpl implements WeekScheduleRepository {
    private final JpaWeekScheduleRepository scheduleRepository;

    public WeekScheduleRepositoryImpl(JpaWeekScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public WeekSchedule save(WeekSchedule schedule) {
        JpaWeekScheduleEntity entity = JpaWeekScheduleEntity.fromDomain(schedule);
        return scheduleRepository.save(entity).toDomain();
    }

    @Override
    public List<WeekSchedule> findAllByOwner(ScheduleOwner owner) {
        return scheduleRepository.findByOwnerOwnerIdAndOwnerOwnerTypeOrderByWeekStartDateDesc(
                        owner.ownerId(), owner.ownerType()
                )
                .stream()
                .map(JpaWeekScheduleEntity::toDomain)
                .toList();
    }

    @Override
    public List<WeekSchedule> findAllByOwnerAndDateRange(ScheduleOwner owner, LocalDate from, LocalDate to) {
        return scheduleRepository.findByOwnerOwnerIdAndWeekStartDateBetweenOrderByWeekStartDateDesc(
                        owner.ownerId(), from, to
                )
                .stream()
                .map(JpaWeekScheduleEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<WeekSchedule> findById(WeekScheduleId id) {
        return scheduleRepository.findById(id.id())
                .map(JpaWeekScheduleEntity::toDomain);
    }

    @Override
    public Optional<WeekSchedule> findByOwnerAndWeekStartDate(ScheduleOwner owner, LocalDate weekStartDate) {
        return scheduleRepository.findByOwnerOwnerIdAndOwnerOwnerTypeAndWeekStartDate(
                        owner.ownerId(), owner.ownerType(), weekStartDate
                )
                .map(JpaWeekScheduleEntity::toDomain);
    }

    @Override
    public void delete(WeekSchedule schedule) {
        scheduleRepository.delete(JpaWeekScheduleEntity.fromDomain(schedule));
    }

    @Override
    @Transactional
    public void deleteAllScheduledRecipesFromUser(ScheduleOwner owner, UserId userId) {
        scheduleRepository.deleteAllByOwnerAndRecipeUser(owner.ownerId(), userId.id());
    }

    @Override
    @Transactional
    public void deleteByRecipeIdAndOwnerType(RecipeId recipeId, ScheduleOwnerType ownerType) {
        scheduleRepository.deleteByRecipeIdAndOwnerType(recipeId.id(), ownerType);
    }
}
