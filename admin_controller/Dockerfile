FROM openjdk:8
WORKDIR ./app
ADD ./target/admin_controller.jar application.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "application.jar"]

