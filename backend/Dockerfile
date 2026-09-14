# Build stage: compile the Ktor fat jar with the Gradle wrapper from the repo root.
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY gradlew gradlew.bat settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY backend ./backend
# --configure-on-demand keeps Gradle from evaluating :app (which needs an Android SDK
# this image does not have) since :backend does not depend on it.
RUN chmod +x gradlew && ./gradlew :backend:buildFatJar --no-daemon --configure-on-demand

# Runtime stage: just the JRE and the fat jar.
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/backend/build/libs/*-all.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
