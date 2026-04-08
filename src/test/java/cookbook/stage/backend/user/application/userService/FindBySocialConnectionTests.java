package cookbook.stage.backend.user.application.userService;

import cookbook.stage.backend.user.application.UserService;
import cookbook.stage.backend.user.shared.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FindBySocialConnectionTests {

    @Autowired
    private UserService userService;

    @Test
    void shouldReturnUserWhenSocialConnectionExists() {
        userService.autoSaveAfterLogin(
                "test@example.com",
                "Test User",
                "google",
                "google-123"
        );

        Optional<User> result = userService.findBySocialConnection("google", "google-123");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        assertThat(result.get().getDisplayName()).isEqualTo("Test User");
    }

    @Test
    void shouldReturnEmptyWhenProviderDoesNotMatch() {
        userService.autoSaveAfterLogin(
                "test@example.com",
                "Test User",
                "google",
                "google2-123"
        );

        Optional<User> result = userService.findBySocialConnection("facebook", "google-123");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenProviderIdDoesNotMatch() {
        userService.autoSaveAfterLogin(
                "test@example.com",
                "Test User",
                "google",
                "google3-123"
        );

        Optional<User> result = userService.findBySocialConnection("google", "different-id");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenNoSocialConnectionExists() {
        Optional<User> result = userService.findBySocialConnection("google", "nonexistent");

        assertThat(result).isEmpty();
    }
}
