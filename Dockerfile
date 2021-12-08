FROM adoptopenjdk/openjdk11:alpine-jre
COPY build/libs/mocap-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]