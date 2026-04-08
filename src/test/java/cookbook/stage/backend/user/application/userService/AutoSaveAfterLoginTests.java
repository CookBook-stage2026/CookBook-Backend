package cookbook.stage.backend.user.application.userService;

import cookbook.stage.backend.user.application.UserService;
import cookbook.stage.backend.user.domain.UserRepository;
import cookbook.stage.backend.user.shared.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AutoSaveAfterLoginTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateAndPersistNewUser() {
        User user = userService.autoSaveAfterLogin(
                "new@example.com",
                "New User",
                "google",
                "google-456"
        );

        assertThat(user.getId()).isNotNull();
        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(user.getDisplayName()).isEqualTo("New User");
        assertThat(user.getSocialConnections())
                .hasSize(1)
                .anyMatch(conn -> conn.getProvider().equals("google")
                        &&
                        conn.getProviderId().equals("google-456"));

        assertThat(userRepository.findById(user.getId())).isPresent();
    }

    @Test
    void shouldCreateUserWithMultipleSocialConnectionsWhenCalledRepeatedly() {
        User user1 = userService.autoSaveAfterLogin(
                "same@example.com",
                "Same User",
                "google",
                "google-123"
        );
        User user2 = userService.autoSaveAfterLogin(
                "same@example.com",
                "Same User",
                "facebook",
                "fb-456"
        );

        assertThat(user1.getId()).isNotEqualTo(user2.getId());
        assertThat(userRepository.findById(user1.getId())).isNotNull();
        assertThat(userRepository.findById(user2.getId())).isNotNull();
    }
}
