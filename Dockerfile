# Stage 1: Build JAR con Maven y Java 21
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw

COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime liviano JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usuario no root por seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /app/target/ms2_flotas_backend-*.jar app.jar
RUN chown -R appuser:appgroup /app

USER appuser
EXPOSE 8082

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
