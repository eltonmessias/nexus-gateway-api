# ---- Build stage: compile the whole reactor, produce the gateway fat jar ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Copy everything and build only the runnable module (nexus-gateway) plus its
# dependencies (-am). Tests are skipped here; run them in CI before deploying.
COPY . .
RUN mvn -B -pl nexus-gateway -am clean package -DskipTests

# ---- Runtime stage: slim JRE, non-root ----
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# Run as an unprivileged user
RUN groupadd -r nexus && useradd -r -g nexus nexus

COPY --from=build /build/nexus-gateway/target/nexus-gateway-*.jar app.jar
USER nexus

EXPOSE 8080

# Respect container memory limits; fail fast on OOM
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-XX:+ExitOnOutOfMemoryError", "-jar", "app.jar"]
