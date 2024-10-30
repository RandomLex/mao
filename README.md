# Song and Resource Services

## Prerequisites

- Java 21 or higher
- Gradle 8.0 or higher
- Docker (to be run)
- Postman

## Setup

1. **Clone the repository:**
    ```sh
    git clone TBD
    cd mao
    ```

2. **Build services:**
   ```sh
     ./gradlew clean build
   ```

3. **Run services:**
   - with the console output
      ```sh
      docker-compose up
      ```
   - to be run in the background
      ```sh
      docker-compose up -d
      ```

## Using Postman Collection

1. **Import the Postman collection:**
    - Open Postman.
    - Click on `Import` in the top left corner.
    - Select the `mao.postman_collection.json` file located in the **postman** of the project.

2. **Run the requests:**
    - Use the imported collection to interact with the Song and Resource services.
    - Ensure the services are running before making requests.

## Endpoints

### Resource Service

- **GET /resources/{id}**: Retrieve a resource by its ID.
- **POST /resources**: Create a new resource.
- **DELETE /resources?1,2**: Delete resources by IDs.

### Song Service

- **GET /songs/{id}**: Retrieve a song by its ID.
- **POST /songs**: Create a new song.
- **DELETE /songs?1,2**: Delete songs by IDs.

