package be.xplore.cookbook.core.repository;

import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.user.UserPreferences;

import java.util.Optional;

public interface UserPreferenceRepository {
    Optional<UserPreferences> findPreferences(User user);

    UserPreferences save(UserPreferences preferences);
}
