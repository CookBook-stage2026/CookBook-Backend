package be.xplore.cookbook.core.repository;

import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.user.UserPreferences;

import java.util.Optional;

public interface UserPreferenceRepository {
    Optional<UserPreferences> findPreferences(UserId userId);

    UserPreferences save(UserPreferences preferences);
}
