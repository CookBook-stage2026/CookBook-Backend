package be.xplore.cookbook.core.service;

import be.xplore.cookbook.core.domain.exception.NotFoundException;
import be.xplore.cookbook.core.domain.user.SocialConnection;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.repository.UserRepository;
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
}
