package cookbook.stage.backend.user.application;

import cookbook.stage.backend.user.domain.SocialConnection;
import cookbook.stage.backend.user.domain.UserRepository;
import cookbook.stage.backend.user.shared.User;
import cookbook.stage.backend.user.shared.UserApi;
import cookbook.stage.backend.user.shared.UserId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserService implements UserApi {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findById(UserId id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findBySocialConnection(String provider, String providerId) {
        return userRepository.findBySocialConnection(provider, providerId);
    }

    @Override
    public User autoSaveAfterLogin(String email, String name, String provider, String providerId) {
        User user = new User(email, name, new ArrayList<>());
        user.getSocialConnections().add(new SocialConnection(provider, providerId, user));
        return userRepository.save(user);
    }
}
