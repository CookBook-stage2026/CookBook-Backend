package be.xplore.cookbook;

import be.xplore.cookbook.rest.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackendApplicationTests extends BaseIntegrationTest {

    @Test
    void contextLoads() {
    }

    @Override
    protected String[] getTablesToClear() {
        return new String[0];
    }
}
