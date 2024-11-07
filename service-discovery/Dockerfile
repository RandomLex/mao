FROM amazoncorretto:21-alpine-jdk AS build
WORKDIR /app

COPY ./gradle /app/gradle
COPY ./gradlew /app/gradlew

#Make gradlew executable and download gradle binaries
RUN chmod +x ./gradlew \
    && ./gradlew --no-daemon --version

COPY ./settings.gradle ./build.gradle /app/
COPY ./service-discovery/build.gradle /app/service-discovery/build.gradle

RUN ./gradlew --no-daemon -p service-discovery dependencies

COPY ./service-discovery/src /app/service-discovery/src
RUN ./gradlew --no-daemon -p service-discovery assemble -x test

FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
COPY --from=build /app/service-discovery/build/libs/*.jar /app/app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]