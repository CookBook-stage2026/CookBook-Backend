package cookbook.stage.backend.domain.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findBySocialConnection(String provider, String providerId);
    Optional<User> findById(UserId id);
    User saveUser(User user);
    List<WeekSchedule> findWeekScheduleByUserId(UserId userId);
    WeekSchedule saveWeekSchedule(WeekSchedule schedule, UserId userId);
}
