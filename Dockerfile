FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY gradle gradle
COPY gradlew .
COPY build.gradle.kts settings.gradle.kts ./
RUN ./gradlew dependencies --no-daemon || true
COPY src src
RUN ./gradlew clean build -x test --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:InitialRAMPercentage=50.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
