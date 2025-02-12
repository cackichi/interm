FROM eclipse-temurin:17-jre-alpine AS base
WORKDIR /opt/app
COPY common-files .
RUN mkdir -p target && \
    chmod -R 777 target

FROM base AS eureka-server
ARG JAR_FILE=eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM base AS rides-service
ARG JAR_FILE=rides-service/target/rides-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM base AS driver-service
ARG JAR_FILE=driver-service/target/driver-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM base AS passenger-service
ARG JAR_FILE=passenger-service/target/passenger-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM base AS gateway-service
ARG JAR_FILE=gateway-service/target/gateway-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]