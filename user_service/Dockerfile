FROM eclipse-temurin:21-jdk
WORKDIR /user_service
COPY target/user_service-0.0.1-SNAPSHOT.jar user_service.jar
ENTRYPOINT ["java","-jar","user_service.jar"]
EXPOSE 8080