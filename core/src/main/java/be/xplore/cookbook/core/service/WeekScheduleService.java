package be.xplore.cookbook.core.service;

import be.xplore.cookbook.core.domain.exception.UserNotFoundException;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.weekschedule.DaySchedule;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;
import be.xplore.cookbook.core.domain.weekschedule.WeekScheduleId;
import be.xplore.cookbook.core.repository.UserRepository;
import be.xplore.cookbook.core.repository.WeekScheduleRepository;

import java.util.List;

public class WeekScheduleService {
    private final WeekScheduleRepository repo;
    private final UserRepository userRepository;

    public WeekScheduleService(WeekScheduleRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    public WeekSchedule saveWeekSchedule(List<DaySchedule> daySchedules, UserId userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        WeekSchedule weekSchedule = new WeekSchedule(WeekScheduleId.create(), user, daySchedules);
        return repo.save(weekSchedule);
    }

    public WeekSchedule findByUserId(UserId userId) {
        return repo.findForUser(userId);
    }
}
