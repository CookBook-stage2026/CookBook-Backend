package be.xplore.cookbook.jpa.repository.schedule;

import be.xplore.cookbook.core.domain.exception.NotFoundException;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;
import be.xplore.cookbook.core.repository.WeekScheduleRepository;
import be.xplore.cookbook.jpa.repository.schedule.entity.JpaWeekScheduleEntity;
import org.springframework.stereotype.Repository;

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
    public WeekSchedule findForUser(UserId userId) {
        var result = weekScheduleRepository.findWeekScheduleByUserId(userId.id());
        if (result.isPresent()) {
            return result.get().toDomain();
        }
        throw new NotFoundException("No week schedule found for user " + userId.id());
    }
}
