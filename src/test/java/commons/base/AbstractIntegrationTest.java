package commons.base;

import com.api.starwars.StarwarsApplication;
import commons.config.MongoDBContainerTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = {"test", "base"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {StarwarsApplication.class, MongoDBContainerTest.class})
@ContextConfiguration(classes = MongoDBContainerTest.class, initializers = MongoDBContainerTest.Initializer.class)
public abstract class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

}