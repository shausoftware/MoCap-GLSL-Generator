FROM openjdk:11.0-jdk-alpine
ARG JAR_FILE=build/libs/mocap-1.0.0.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]