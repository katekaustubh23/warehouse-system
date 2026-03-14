FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /build

# copy entire project so parent pom is available
COPY . .

# build only this module
RUN mvn -pl warehouse-gateway -am clean package -DskipTests


FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /build/warehouse-gateway/target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]