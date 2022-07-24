FROM  registry.hub.docker.com/library/openjdk:17.0.1-jdk-slim

EXPOSE 8080

COPY build/libs/*.jar /spring-boot-application.jar

ENTRYPOINT ["java","-jar","/spring-boot-application.jar"]