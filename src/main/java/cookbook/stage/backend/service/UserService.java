package cookbook.stage.backend.service;

import cookbook.stage.backend.domain.exception.NotFoundException;
import cookbook.stage.backend.domain.user.SocialConnection;
import cookbook.stage.backend.domain.user.User;
import cookbook.stage.backend.domain.user.UserId;
import cookbook.stage.backend.domain.user.UserPreferences;
import cookbook.stage.backend.domain.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findById(UserId id) {
        if (id == null) {
            throw new NotFoundException("User ID cannot be null");
        }
        return userRepository.findById(id);
    }

    public Optional<User> findBySocialConnection(String provider, String providerId) {
        return userRepository.findBySocialConnection(provider, providerId);
    }

    @Transactional
    public User autoSaveAfterLogin(String email, String name, String provider, String providerId) {
        User user = new User(email, name, new ArrayList<>());
        user.getSocialConnections().add(new SocialConnection(provider, providerId));
        return userRepository.save(user);
    }

    public UserPreferences findPreferences(UserId userId) {
        return userRepository.findPreferences(userId);
    }

    @Transactional
    public void updatePreferences(UserId userId, UserPreferences preferences) {
        userRepository.updatePreferences(userId, preferences);
    }
}
