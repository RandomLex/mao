FROM amazoncorretto:21-alpine-jdk AS build
WORKDIR /app

COPY ./gradle /app/gradle
COPY ./gradlew /app/gradlew

#Make gradlew executable and download gradle binaries
RUN chmod +x ./gradlew \
    && ./gradlew --no-daemon --version

COPY ./settings.gradle ./build.gradle /app/
COPY ./api-gateway/build.gradle /app/api-gateway/build.gradle

RUN ./gradlew --no-daemon -p api-gateway dependencies

COPY ./api-gateway/src /app/api-gateway/src
RUN ./gradlew --no-daemon -p api-gateway assemble -x test

FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
COPY --from=build /app/api-gateway/build/libs/*.jar /app/app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]