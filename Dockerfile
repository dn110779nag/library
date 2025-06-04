# syntax=docker/dockerfile:1
FROM eclipse-temurin:21-jre as builder
ARG JAR_FILE=./target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract
FROM eclipse-temurin:21-jre
COPY application-prod.properties application.properties
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./
#RUN ls -l
CMD pwd && ls -l && java org.springframework.boot.loader.launch.JarLauncher
#ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "org.springframework.boot.loader.launch.JarLauncher"]
