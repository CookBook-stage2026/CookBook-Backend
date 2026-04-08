package cookbook.stage.backend.user.application.userService;

import cookbook.stage.backend.shared.domain.NotFoundException;
import cookbook.stage.backend.user.application.UserService;
import cookbook.stage.backend.user.shared.User;
import cookbook.stage.backend.user.shared.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class FindByIdTests {

    @Autowired
    private UserService userService;

    @Test
    void shouldReturnUserWhenUserExists() {
        User savedUser = userService.autoSaveAfterLogin(
                "test@example.com",
                "Test User",
                "google",
                "google-123"
        );

        Optional<User> result = userService.findById(savedUser.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        assertThat(result.get().getDisplayName()).isEqualTo("Test User");
    }

    @Test
    void shouldReturnEmptyWhenUserDoesNotExist() {
        UserId nonExistentId = new UserId(java.util.UUID.randomUUID());

        Optional<User> result = userService.findById(nonExistentId);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenIdIsNull() {
        assertThatThrownBy(() -> userService.findById(null))
                .isInstanceOf(NotFoundException.class);
    }
}
