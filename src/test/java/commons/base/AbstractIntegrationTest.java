package commons.base;

import com.api.starwars.StarwarsApplication;
import com.api.starwars.auth.jwt.model.mongo.MongoUser;
import com.api.starwars.commons.config.database.mongo.MongoConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = {"test", "base"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {StarwarsApplication.class, MongoConfiguration.class})
public abstract class AbstractIntegrationTest {

    protected final MongoUser APPLICATION_USER = new MongoUser(
            "another_application_who_consumes_this_api",
            "$2a$10$W1NqIc3gvpLNwQska2iAFOQ1FOpUDQ1a5FF.ffAF2eUNuaLrr3FKm",
            List.of("PLANET_LIST", "PLANET_CREATE", "PLANET_UPDATE", "PLANET_DELETE")
    );
    protected final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhbm90aGVyX2FwcGxpY2F0aW9uX3dob19jb25zdW1lc190aGlzX2FwaSJ9.jnSWQTkg6dQ18tAPl8RS2JrdEdmtxBvx40Tq7WqYFighnziLKzUi2BLJ4S__dOlQDuJl0Lw3NYFS5IbGgd-XnQ";
    @Autowired
    protected MockMvc mockMvc;
}