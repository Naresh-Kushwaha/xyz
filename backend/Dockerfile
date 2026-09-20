# ---- Build stage ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B -q clean package -DskipTests

# ---- Run stage ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app


RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75", \
  "-jar", "app.jar"]

