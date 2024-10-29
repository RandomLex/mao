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
