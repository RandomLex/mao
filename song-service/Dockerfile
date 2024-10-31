FROM amazoncorretto:21-alpine-jdk AS build
WORKDIR /app

COPY ./gradle /app/gradle
COPY ./gradlew /app/gradlew

#Make gradlew executable and download gradle binaries
RUN chmod +x ./gradlew \
    && ./gradlew --no-daemon --version

COPY ./settings.gradle ./build.gradle /app/
COPY ./song-service/build.gradle /app/song-service/build.gradle

RUN ./gradlew --no-daemon -p song-service dependencies

COPY ./song-service/src /app/song-service/src
RUN ./gradlew --no-daemon -p song-service assemble -x test

FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
COPY --from=build /app/song-service/build/libs/*.jar /app/app.jar

EXPOSE 8082

CMD ["java", "-jar", "app.jar"]