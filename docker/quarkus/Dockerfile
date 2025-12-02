# Quarkus REST API Dockerfile
FROM eclipse-temurin:17-jdk as builder

WORKDIR /app

# Copy Maven wrapper and pom files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Copy ALL module pom.xml files
COPY hotel-ejb-api/pom.xml hotel-ejb-api/
COPY hotel-ejb/pom.xml hotel-ejb/
COPY hotel-ear/pom.xml hotel-ear/
COPY hotel-rest/pom.xml hotel-rest/

# Create empty directories for modules we don't need to build fully
RUN mkdir -p hotel-ejb/src/main/java hotel-ear/src/main/application

# Download dependencies
RUN chmod +x mvnw && ./mvnw dependency:go-offline -pl hotel-ejb-api,hotel-rest -am -B || true

# Copy source code
COPY hotel-ejb-api/src hotel-ejb-api/src
COPY hotel-rest/src hotel-rest/src

# Build the application (only hotel-ejb-api and hotel-rest)
RUN ./mvnw package -pl hotel-ejb-api,hotel-rest -am -DskipTests -B

# Runtime image
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built artifact
COPY --from=builder /app/hotel-rest/target/quarkus-app/lib/ /app/lib/
COPY --from=builder /app/hotel-rest/target/quarkus-app/*.jar /app/
COPY --from=builder /app/hotel-rest/target/quarkus-app/app/ /app/app/
COPY --from=builder /app/hotel-rest/target/quarkus-app/quarkus/ /app/quarkus/

EXPOSE 8081

ENV JAVA_OPTS="-Djava.util.logging.manager=org.jboss.logmanager.LogManager"

CMD ["java", "-jar", "/app/quarkus-run.jar"]