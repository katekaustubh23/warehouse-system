FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /build

# 👇 1. Accept build arguments
ARG GITHUB_USERNAME
ARG GITHUB_TOKEN

# 👇 2. Inject Maven credentials (VERY IMPORTANT)
RUN mkdir -p /root/.m2 && echo "<settings><servers><server><id>github</id><username>${GITHUB_USERNAME}</username><password>${GITHUB_TOKEN}</password></server></servers></settings>" > /root/.m2/settings.xml

# copy entire project so parent pom is available
COPY . .

# build only this module from parent pom
RUN mvn -pl orders-service-jdbc -am clean package -DskipTests


FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /build/orders-service-jdbc/target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]