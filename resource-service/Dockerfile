FROM amazoncorretto:21-alpine-jdk AS build
WORKDIR /app

COPY ./gradle /app/gradle
COPY ./gradlew /app/gradlew

#Make gradlew executable and download gradle binaries
RUN chmod +x ./gradlew \
    && ./gradlew --no-daemon --version

COPY ./settings.gradle ./build.gradle /app/
COPY ./resource-service/build.gradle /app/resource-service/build.gradle

RUN ./gradlew --no-daemon -p resource-service dependencies

COPY ./resource-service/src /app/resource-service/src
RUN ./gradlew --no-daemon -p resource-service assemble -x test

FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
COPY --from=build /app/resource-service/build/libs/*.jar /app/app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]