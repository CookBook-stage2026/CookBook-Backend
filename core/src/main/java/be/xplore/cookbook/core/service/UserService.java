package be.xplore.cookbook.core.service;

import be.xplore.cookbook.core.domain.exception.NotFoundException;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserPreferences;
import be.xplore.cookbook.core.domain.user.command.AutoSaveAfterLoginCommand;
import be.xplore.cookbook.core.domain.user.command.FindUserByIdQuery;
import be.xplore.cookbook.core.domain.user.command.FindUserBySocialConnectionQuery;
import be.xplore.cookbook.core.repository.UserPreferenceRepository;
import be.xplore.cookbook.core.repository.UserRepository;

import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;

    public UserService(UserRepository userRepository, UserPreferenceRepository userPreferenceRepository) {
        this.userRepository = userRepository;
        this.userPreferenceRepository = userPreferenceRepository;
    }

    public Optional<User> findById(FindUserByIdQuery query) {
        if (query.userId() == null) {
            throw new NotFoundException("User ID cannot be null");
        }
        return userRepository.findById(query.userId());
    }

    public Optional<User> findBySocialConnection(FindUserBySocialConnectionQuery query) {
        return userRepository.findBySocialConnection(query.provider(), query.providerId());
    }

    public User autoSaveAfterLogin(AutoSaveAfterLoginCommand command) {
        User user = new User(command.email(), command.name(), command.provider(), command.providerId());
        User savedUser = userRepository.save(user);
        userPreferenceRepository.save(UserPreferences.empty(savedUser));
        return savedUser;
    }
}
