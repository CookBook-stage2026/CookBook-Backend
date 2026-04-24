package cookbook.stage.backend.repository;

import cookbook.stage.backend.domain.exception.NotFoundException;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.domain.weekschedule.WeekSchedule;
import cookbook.stage.backend.domain.weekschedule.WeekScheduleRepository;
import cookbook.stage.backend.repository.jpa.schedule.JpaWeekScheduleEntity;
import cookbook.stage.backend.repository.jpa.schedule.JpaWeekScheduleRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class WeekScheduleRepositoryImpl implements WeekScheduleRepository {
    private final JpaWeekScheduleRepository weekScheduleRepository;

    public WeekScheduleRepositoryImpl(JpaWeekScheduleRepository weekScheduleRepository) {
        this.weekScheduleRepository = weekScheduleRepository;
    }

    @Override
    @Transactional
    public WeekSchedule save(WeekSchedule schedule) {
        JpaWeekScheduleEntity entity = JpaWeekScheduleEntity.fromDomain(schedule);
        return weekScheduleRepository.save(entity).toDomain();
    }

    @Override
    public WeekSchedule findForUser(UserId userId) {
        var result = repo.findWeekScheduleByUser_Id(userId.id());
        if (result.isPresent()) {
            return result.get().toDomain();
        }
        throw new NotFoundException("No week schedule found for user " + userId.id());
    }
}
