FROM amazoncorretto:21-alpine-jdk

EXPOSE 8082

COPY build/docker/app.jar /app/app.jar


CMD ["java", "-jar", "/app/app.jar"]