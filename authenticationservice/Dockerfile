FROM eclipse-temurin:21-jdk
WORKDIR /authentication_service
COPY target/*.jar authentication_service.jar
ENTRYPOINT ["java","-jar","authentication_service.jar"]
EXPOSE 8081