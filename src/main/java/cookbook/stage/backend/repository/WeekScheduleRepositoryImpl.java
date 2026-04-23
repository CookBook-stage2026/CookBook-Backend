package cookbook.stage.backend.repository;

import cookbook.stage.backend.domain.week_schedule.WeekSchedule;
import cookbook.stage.backend.domain.week_schedule.WeekScheduleRepository;
import cookbook.stage.backend.repository.jpa.schedule.JpaWeekScheduleEntity;
import cookbook.stage.backend.repository.jpa.schedule.JpaWeekScheduleRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class WeekScheduleRepositoryImpl implements WeekScheduleRepository {
    private final JpaWeekScheduleRepository repo;

    public WeekScheduleRepositoryImpl(JpaWeekScheduleRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional
    public WeekSchedule save(WeekSchedule schedule) {
        repo.deleteByUser_Id(schedule.user().getId().id());
        repo.flush();

        JpaWeekScheduleEntity entity = JpaWeekScheduleEntity.fromDomain(schedule);
        return repo.save(entity).toDomain();
    }
}
