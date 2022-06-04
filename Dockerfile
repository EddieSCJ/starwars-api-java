# First stage (build)
FROM openjdk:17.0-jdk-slim-buster AS BUILD_IMAGE

# Creating package where will be our application
ENV APP_HOME=/root/dev/myapp/
WORKDIR $APP_HOME

# Copying gradle configs to package
COPY build.gradle gradlew gradlew.bat $APP_HOME
COPY gradle $APP_HOME/gradle

# download dependencies
RUN chmod +x gradlew
RUN ./gradlew build -x :bootJar -x test --continue --no-daemon

# copying dependecies
COPY . .
RUN chmod +x gradlew
RUN ./gradlew :compileJava --no-daemon

EXPOSE 8080
CMD ["./gradlew", "bootJar", "-t", "--no-daemon", "&", "./gradlew","bootRun", "--no-daemon", "-x", ":compileJava", "&&", "fg"]
