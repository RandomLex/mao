## List of comments for version 1
0. Please use a synchronous, sequential approach:
   - Each step — file upload, metadata validation, resource storage, and metadata posting to Song Service — completes in sequence. The client only receives a 200 OK when all steps have fully succeeded, avoiding premature success responses.
   - We need to confirm that Song Service has successfully saved the metadata before Resource Service sends a response to the client. This reduces the risk of data inconsistencies, where a resource might be saved but lacks corresponding metadata in Song Service.
1. uploadResource method (POST /resources):
    - Issue: You are using @RequestPart("file") Mono<FilePart> filePart to accept the file. However, the requirement specifies that the method should accept audio data directly in binary format.
    - Solution: Please change @RequestPart("file") Mono<FilePart> to @RequestBody byte[] audioData to receive the binary audio data directly. Also, specify consumes = "audio/mpeg" in @PostMapping to restrict the request to MP3 files only.
    - Response: The method currently returns an id, but to meet requirements, it should use ResponseEntity to wrap the response with a JSON body format, such as { "id": 1123 }, in the response body.
2. getResourceById method (GET /resources/{id}):
    - Response: The method should return ResponseEntity<byte[]>.
3. deleteResources method (DELETE /resources):
    - Response: The method should return a JSON response in the format { "ids": [1, 2] }. Please wrap this response in ResponseEntity.
4. createSong method (POST /songs):
    - Response: The method should return ResponseEntity<SongCreateResponse>.
5. getSongById method (GET /songs/{id}):
    - Response: The method should return ResponseEntity<SongDto>.
6. deleteSongs method (DELETE /songs):
    - Response: The method should return a JSON format, { "ids": [1, 2] }, wrapped in ResponseEntity.
7. Please don't forget to return meaningful error messages. See the example in the demo (https://epam-my.sharepoint.com/:v:/p/kir_tananushka/EVt46rbQaGZMsgqip9lBziABQgyDsptNMzpXJLYjTRQ6lg?e=M1TIKY).
8. You can also use the Postman collection for testing (https://epam-my.sharepoint.com/:u:/p/kir_tananushka/ETw0-YI8oLBLs5ClWFzJCdQBJaSN0TAnO6TqJTVn9H6HdA?e=8CYhRc).
9. After making the changes, please use Availia Kicker to create a new translation for your updates.
---
## Disclaimer: only comment 1 is relevant, 8th is meaningful but exceeds the task requirements. Comments 2-7 have been already implemented in the version 1.
0. I've provided a WebFlux approach because there were no restrictions for technology. Moreover, as soon as it is REST implementation, it still be synchronous. WebFlux here is just more effective Webserver thread model. 
   - was already provided in the version 1. Look at the com.barzykin.mao.resourceservice.endpoints.ResourceEndpoint#uploadMp3
   - was already provided in the version 1. Look at the sequential approach in the ResourceEndpoint#uploadMp3 and underlaying services.
1. Fixed in the commit b745eafc
2. It was already provided the Mono<ResponseEntity<byte[]> in the version 1.
3. It was already provided the Mono<ResponseEntity<ResourceDeleteResponse>> in the version 1
4. It was already provided the Mono<ResponseEntity<SongCreateResponse>> in the version 1
5. It was already provided the Mono<ResponseEntity<SongDto> in the version 1
6. It was already provided the Mono<ResponseEntity<SongDeleteResponse> in the version 1
7. It was already provided the meaningful error messages in the version 1. Look at the GlobalExceptionHandler-s of the resource-service and song-service
8. Postman collection was provided in the version 1
9. Done
---
## Module 2 version 1
- run the ./gradlew clean build
- run the docker-compose up
- import the ./postman/mao.postman_collection.json
- check the services with the postman collection
---
## List of comments for Module 2 version 1 
- **Grade**: Not Done (0)
- **Recommendations**: Hello Alexej, you did well, but there are a few things to refine.

Dockerfiles  

1. Set working directory

Neither Dockerfile defines a working directory (WORKDIR). This lack of defined directory structure can lead to unpredictable behavior if the file structure changes or if additional commands are added.  

Please add WORKDIR statements in each Dockerfile. This best practice provides a clear directory context for copying files and executing commands. By setting a common working directory, you avoid hard-coding paths in later commands, making the Dockerfile more readable and less error-prone.

2. Use multi-stage builds

The Dockerfiles use single-stage builds, copying pre-built JAR files, which leads to larger image sizes and a less efficient structure. Multi-stage builds, by contrast, can reduce the final image size by separating the build and runtime environments, retaining only essential files.  

Please implement multi-stage builds in the Dockerfiles to reduce image size and improve build efficiency.  

3. While implementing multi-stage build, use dependency caching

Without dependency caching dependencies will be downloaded each time the build process runs, which increases build times.

Please add dependency caching by copying only the build files (build.gradle) first, running a dependency resolution command, and only then copying the source files. This method optimizes the build time by avoiding redundant downloads.

4. Use --no-daemon flag with Gradle

The Dockerfile does not include the --no-daemon flag with Gradle commands, which can lead to unnecessary memory usage since Docker containers are short-lived.

Please add the --no-daemon flag to all Gradle commands in the Dockerfiles to optimize memory consumption within the container environment.

5. Avoid hardcoding JAR file names

Hardcoding specific JAR file names (e.g., app.jar) in the Dockerfile makes it less flexible for future updates.

Please use a wildcard to copy any JAR file in the target directory. This adjustment makes the Dockerfile adaptable to version changes without manual updates.

6. Possible structure of Dockerfiles with multi-stage build  

Build stage Use a lightweight Gradle image to build the application:  
https://hub.docker.com/_/gradle/tags?name=jdk21-alpine Set the working directory inside the container for the build Copy only the Gradle wrapper and build configuration files to cache dependencies Grant execute permissions to the Gradle wrapper Download the dependencies and cache them using --no-daemon flag Copy the source code into the container Compile and package the application without running tests, using the assemble task and --no-daemon and -x test flags Run stage Use a lightweight Alpine image for running the application Set the working directory for the application runtime Copy the built JAR from the build stage into the run stage Expose the application port to the host Define the command to run the application (Java + JAR)

Example:

FROM ... AS build WORKDIR /app  
COPY build.gradle settings.gradle gradlew ./  
COPY gradle ./gradle  
RUN chmod +x gradlew 
RUN ./gradlew dependencies --no-daemon  
COPY src ./src 
RUN ./gradlew assemble --no-daemon -x test  

FROM ... WORKDIR /app  
COPY --from=build build/libs/*.jar app.jar EXPOSE 8080 CMD ["java", "-jar", "app.jar"]

Docker Compose  

7. Use database initialization scripts

According to requirements, you should add init scripts for the database to run when container starts up. However, neither database initialization script is defined.

Please add and mount initialization scripts in the docker-compose.yml file to meet requirements.

Example: volumes: - ./init-scripts/resource-db/:/docker-entrypoint-initdb.d

8. Use Alpine images for third-party dependencies

The postgres service does not use an Alpine-based image, which is part of the task requirements.

Please use an Alpine-based image for the postgres services (e.g., postgres:17-alpine), aligning with requirements for using lightweight images whenever possible.

9. Avoid using host.docker.internal for cross-container communication

The configuration relies on host.docker.internal to connect services to databases, which is not recommended for Docker Compose environments. This method is platform-dependent and can lead to connectivity issues, especially on non-Docker Desktop environments like Linux. Moreover, according to requirements you should use logical service names to cross-reference.

Please modify the configuration to use service names as hostnames for inter-service communication within Docker Compose networks. Docker automatically resolves these names within the same network, adhering to best practices.

10. Use docker-compose up -d --build instead of ./gradlew clean build and docker-compose up

Please simplify the startup process by using docker-compose up -d --build for building and starting the application containers. This single command improves workflow consistency and ensures the latest changes are applied to the images. In the Final Screening, you are highly likely to be offered to use this specific command.

After making the changes, please use Availia Kicker to create a new translation for your updates.

---
## List of changes for Module 2 version 2
1. Done. The WORKDIR was added to the Dockerfiles
2. Done. The multi-stage build is implemented in the Dockerfiles
3. Done. The dependency caching is added to the Dockerfiles.  
Besides the suggestion the additional layer to cache gradle binaries is added.
4. Done. The --no-daemon flag is added to the Gradle commands in the Dockerfiles
5. Done. The wildcard in copying the JAR file is used in the Dockerfiles.
6. Done. The suggestions of the structure of Dockerfiles are used.
7. Done. To meet the task requirements, the init scripts are added to the docker-compose.yaml and pointed to the already exising flyway scripts.
Flyway is turned off.
However, in the real life Flyway provides more flexibility and control over the database schema.
8. Done. The Alpine-based image is used for all the services in Dockerfiles and docker-compose.yaml
9. Done. The host.docker.internal is replaced with the service names in the env-files.  
Moreover, an additional network between services is added to avoid the usage of the default network.
10. Done. The docker-compose up -d --build is used to build and start the services.
