package be.xplore.cookbook.core.repository;

import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.domain.user.UserPreferences;

public interface UserPreferenceRepository {
    UserPreferences findPreferences(UserId userId);

    void updatePreferences(UserId userId, UserPreferences preferences);
}
