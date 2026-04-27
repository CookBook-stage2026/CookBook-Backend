package cookbook.stage.backend.repository;

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
        return weekScheduleRepository.save(entity).toDomain(schedule.user());
    }
}
