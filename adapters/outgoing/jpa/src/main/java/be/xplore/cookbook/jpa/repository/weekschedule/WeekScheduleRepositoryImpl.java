package be.xplore.cookbook.jpa.repository.weekschedule;

import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;
import be.xplore.cookbook.core.domain.weekschedule.WeekScheduleId;
import be.xplore.cookbook.core.repository.WeekScheduleRepository;
import be.xplore.cookbook.jpa.repository.weekschedule.entity.JpaWeekScheduleEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class WeekScheduleRepositoryImpl implements WeekScheduleRepository {
    private final JpaWeekScheduleRepository weekScheduleRepository;

    public WeekScheduleRepositoryImpl(JpaWeekScheduleRepository weekScheduleRepository) {
        this.weekScheduleRepository = weekScheduleRepository;
    }

    @Override
    public WeekSchedule save(WeekSchedule schedule) {
        JpaWeekScheduleEntity entity = JpaWeekScheduleEntity.fromDomain(schedule);
        return weekScheduleRepository.save(entity).toDomain();
    }

    @Override
    public List<WeekSchedule> findAllByUserId(UserId userId) {
        return weekScheduleRepository.findByUserIdOrderByWeekStartDateDesc(userId.id()).stream()
                .map(JpaWeekScheduleEntity::toDomain)
                .toList();
    }

    @Override
    public List<WeekSchedule> findAllByUserIdAndDateRange(UserId userId, LocalDate from, LocalDate to) {
        return weekScheduleRepository.findByUserIdAndWeekStartDateBetweenOrderByWeekStartDateDesc(
                        userId.id(), from, to).stream()
                .map(JpaWeekScheduleEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<WeekSchedule> findById(WeekScheduleId id) {
        return weekScheduleRepository.findById(id.id())
                .map(JpaWeekScheduleEntity::toDomain);
    }

    @Override
    public Optional<WeekSchedule> findByUserIdAndWeekStartDate(UserId userId, LocalDate weekStartDate) {
        return weekScheduleRepository.findByUserIdAndWeekStartDate(userId.id(), weekStartDate)
                .map(JpaWeekScheduleEntity::toDomain);
    }

    @Override
    public void deleteById(WeekScheduleId id) {
        weekScheduleRepository.deleteById(id.id());
    }
}
