FROM eclipse-temurin:17-jre-alpine AS builder
WORKDIR /opt/app
ARG JAR_FILE=target/eureka-server-*.jar
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:17-jre-alpine
WORKDIR /opt/app
COPY --from=builder /opt/app/dependencies/ ./
COPY --from=builder /opt/app/snapshot-dependencies/ ./
COPY --from=builder /opt/app/spring-boot-loader/ ./
COPY --from=builder /opt/app/application/ ./
EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]