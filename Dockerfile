FROM openjdk:11-jdk AS BUILD_IMAGE
ENV APP_HOME=/root/dev/myapp/
RUN mkdir -p $APP_HOME/src/main/java
WORKDIR $APP_HOME
COPY build.gradle gradlew gradlew.bat $APP_HOME
COPY gradle $APP_HOME/gradle
# download dependencies
RUN ./gradlew build -x :bootJar -x test --continue
COPY . .
RUN ./gradlew build

FROM openjdk:11-jre
WORKDIR /root/
COPY --from=BUILD_IMAGE /root/dev/myapp/build/libs/*.jar .
EXPOSE 8080
CMD ["java","-jar","starwars-0.0.1-SNAPSHOT.jar"]