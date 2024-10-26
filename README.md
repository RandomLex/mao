# Song and Resource Services

## Prerequisites

- Java 21 or higher
- Gradle 8.0 or higher
- Docker
- Postman

## Setup

1. **Clone the repository:**
    ```sh
    git clone TBD
    cd song-resource-solution
    ```

2. **Start the databases:**
   - Gradle plugins will be used to manage the databases when you run either **gradle** **build** or **bootRun**.
   - PostgreSQL instances will be started automatically using Docker containers.
   - The databases will be created automatically using `docker-compose.yaml` files started by Gradle plugins.
   - The tables in the databases will be initialized automatically using Flyway migrations.
   - Disclaimer: containers won't be stopped automatically or deleted after the services are stopped.
   - You can stop and delete them manually using from the root project directory:
   - ```sh
     ./gradlew composeDown
     ```

3. **Build the project:**
    ```sh
    ./gradlew build
    ```

## Running the Services

1. **Navigate to the root project directory:**

### Song Service

1. **Run the Song Service:**
    ```sh
    ./gradlew :song-service:bootRun
    ```

### Resource Service

1. **Run the Resource Service:**
    ```sh
    ./gradlew :resource-service:bootRun
    ```
## Alternatively, you can run both services at once but sometimes it doesn't work properly

1. **Run the Song Service:**
    ```sh
    ./gradlew bootRun
    ```

## Using Postman Collection

1. **Import the Postman collection:**
    - Open Postman.
    - Click on `Import` in the top left corner.
    - Select the `postman_collection.json` file located in the root of the project.

2. **Configure the environment:**
    - Set up a new environment in Postman with the following variables:
        - `song_service_url`: `http://localhost:8082`
        - `resource_service_url`: `http://localhost:8081`

3. **Run the requests:**
    - Use the imported collection to interact with the Song and Resource services.
    - Ensure the services are running before making requests.

## Endpoints

### Resource Service

- **GET /resources/{id}**: Retrieve a resource by its ID.
- **POST /resources**: Create a new resource.
- **DELETE /resources**: Delete resources by IDs.

### Song Service

- **GET /songs/{id}**: Retrieve a song by its ID.
- **POST /songs**: Create a new song.
- **DELETE /songs**: Delete songs by IDs.

## Running Tests

1. **Navigate to the service directory:**
    ```sh
    cd song-service
    ````
    #### or
    ```sh
    cd resource-service
    ```

2. **Run the tests:**
    ```sh
    ./gradlew test
    ```
