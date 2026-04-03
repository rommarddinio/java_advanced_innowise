FROM eclipse-temurin:21-jdk
WORKDIR /order_service
COPY target/*.jar order_service.jar
ENTRYPOINT ["java","-jar","order_service.jar"]
EXPOSE 8082