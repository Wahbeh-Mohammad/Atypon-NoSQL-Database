FROM openjdk:8
WORKDIR ./app
ADD ./target/read_controller.jar application.jar
EXPOSE 8001
ENTRYPOINT ["java", "-jar", "application.jar"]

