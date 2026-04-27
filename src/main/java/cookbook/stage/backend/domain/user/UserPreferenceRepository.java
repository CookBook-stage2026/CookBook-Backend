package cookbook.stage.backend.domain.user;

public interface UserPreferenceRepository {
    UserPreferences findPreferences(UserId userId);

    void updatePreferences(UserId userId, UserPreferences preferences);
}
