package cookbook.stage.backend.repository;

import cookbook.stage.backend.domain.user.User;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.domain.user.UserRepository;
import cookbook.stage.backend.domain.user.WeekSchedule;
import cookbook.stage.backend.repository.jpa.user.JpaUserEntity;
import cookbook.stage.backend.repository.jpa.user.JpaUserRepository;
import cookbook.stage.backend.repository.jpa.user.JpaWeekScheduleEntity;
import cookbook.stage.backend.repository.jpa.user.JpaWeekScheduleRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;
    private final JpaWeekScheduleRepository jpaWeekScheduleRepository;

    public UserRepositoryImpl(JpaUserRepository jpaUserRepository, JpaWeekScheduleRepository jpaWeekScheduleRepository) {
        this.jpaUserRepository = jpaUserRepository;
        this.jpaWeekScheduleRepository = jpaWeekScheduleRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email).map(JpaUserEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findBySocialConnection(String provider, String providerId) {
        return jpaUserRepository.findBySocialConnection(provider, providerId).map(JpaUserEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(UserId id) {
        return jpaUserRepository.findById(id.id()).map(JpaUserEntity::toDomain);
    }

    @Override
    public User saveUser(User user) {
        return jpaUserRepository.save(JpaUserEntity.fromDomain(user)).toDomain();
    }

    @Override
    public List<WeekSchedule> findWeekScheduleByUserId(UserId userId) {
        return jpaWeekScheduleRepository.findByUserId(userId.id())
                .stream()
                .map(JpaWeekScheduleEntity::toDomain)
                .toList();
    }

    @Override
    public WeekSchedule saveWeekSchedule(WeekSchedule schedule, UserId userId) {
        JpaWeekScheduleEntity entity = JpaWeekScheduleEntity.fromDomain(schedule, userId.id());
        return jpaWeekScheduleRepository.save(entity).toDomain();
    }
}
