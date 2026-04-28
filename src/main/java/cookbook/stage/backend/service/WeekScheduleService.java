package cookbook.stage.backend.service;

import cookbook.stage.backend.domain.exception.UserNotFoundException;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.domain.weekschedule.DaySchedule;
import cookbook.stage.backend.domain.weekschedule.WeekSchedule;
import cookbook.stage.backend.domain.weekschedule.WeekScheduleId;
import cookbook.stage.backend.domain.weekschedule.WeekScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeekScheduleService {
    private final WeekScheduleRepository repo;
    private final UserService userService;

    public WeekScheduleService(WeekScheduleRepository repo, UserService userService) {
        this.repo = repo;
        this.userService = userService;
    }

    public WeekSchedule saveWeekSchedule(List<DaySchedule> daySchedules, UserId userId) {
        var user = userService.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        WeekSchedule weekSchedule = new WeekSchedule(WeekScheduleId.create(), user, daySchedules);
        return repo.save(weekSchedule);
    }

    public WeekSchedule findByUserId(UserId userId) {
        return repo.findForUser(userId);
    }
}
