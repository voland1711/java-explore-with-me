FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar app.jar
CMD ["java", "-jar","-Dspring.profiles.active=production", "app.jar"]