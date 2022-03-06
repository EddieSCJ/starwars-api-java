package commons.config;


import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@TestConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MongoDBContainerTest {

    private static final Integer MONGO_PORT = 27017;
    private static final String DEFAULT_IMAGE_AND_TAG = "mongo:5.0.6";

    @Container
    private static final GenericContainer mongoContainer =
            new GenericContainer<>(DockerImageName.parse(DEFAULT_IMAGE_AND_TAG))
            .withExposedPorts(MONGO_PORT);

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
            mongoContainer.start();

            TestPropertyValues values = TestPropertyValues.of(
                    "spring.data.mongodb.host=" + mongoContainer.getContainerIpAddress(),
                    "spring.data.mongodb.port=" + mongoContainer.getFirstMappedPort()
            );

            values.applyTo(applicationContext);
        }
    }

}
