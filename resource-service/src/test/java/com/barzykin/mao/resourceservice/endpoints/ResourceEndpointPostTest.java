package com.barzykin.mao.resourceservice.endpoints;


import com.barzykin.mao.resourceservice.dto.ErrorResponse;
import com.barzykin.mao.resourceservice.dto.SongCreateResponse;
import com.barzykin.mao.resourceservice.exceptions.InvalidFileException;
import com.barzykin.mao.resourceservice.services.FilePartService;
import com.barzykin.mao.resourceservice.services.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = ResourceEndpoint.class)
class ResourceEndpointPostTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private FilePartService filePartService;

    @MockBean
    private ResourceService resourceService;

    @BeforeEach
    void setUp() {
        Mockito.reset(filePartService, resourceService);
    }

    @Test
    void uploadMp3_success() {
        byte[] validMp3Data = new byte[]{/* mp3 data bytes */};
        Mockito.when(filePartService.extractBytesFromFilePart(Mockito.any())).thenReturn(Mono.just(validMp3Data));
        Mockito.when(resourceService.validateMp3File(validMp3Data)).thenReturn(Mono.just(validMp3Data));
        Mockito.when(resourceService.saveResourceAndPostToSongService(validMp3Data)).thenReturn(Mono.just(1));

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", validMp3Data)
            .header("Content-Disposition", "form-data; name=\"file\"; filename=\"test.mp3\"")
            .header("Content-Type", "audio/mpeg");
        MultiValueMap<String, HttpEntity<?>> multipartData = bodyBuilder.build();

        webTestClient.post()
            .uri("/resources")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .bodyValue(multipartData)
            .exchange()
            .expectStatus().isOk()
            .expectBody(SongCreateResponse.class)
            .value(response -> {
                assert response.id() == 1L;
            });
    }

    @Test
    void uploadMp3_badRequest() {
        byte[] invalidMp3Data = new byte[]{/* non-mp3 data bytes */};
        Mockito.when(filePartService.extractBytesFromFilePart(Mockito.any()))
            .thenReturn(Mono.just(invalidMp3Data));
        Mockito.when(resourceService.validateMp3File(invalidMp3Data))
            .thenReturn(Mono.error(new InvalidFileException("File is not a valid MP3")));

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", invalidMp3Data)
            .header("Content-Disposition", "form-data; name=\"file\"; filename=\"test_invalid.mp3\"")
            .header("Content-Type", "application/octet-stream");
        MultiValueMap<String, HttpEntity<?>> multipartData = bodyBuilder.build();

        webTestClient.post()
            .uri("/resources")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .bodyValue(multipartData)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(ErrorResponse.class)
            .isEqualTo(new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "File is not a valid MP3"
            ));
    }

    @Test
    void uploadMp3_internalServerError() {
        byte[] validMp3Data = new byte[]{/* mp3 data bytes */};
        Mockito.when(filePartService.extractBytesFromFilePart(Mockito.any())).thenReturn(Mono.just(validMp3Data));
        Mockito.when(resourceService.validateMp3File(validMp3Data)).thenReturn(Mono.error(new RuntimeException("Internal server error")));

        webTestClient.post()
            .uri("/resources")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .exchange()
            .expectStatus().isEqualTo(500)
            .expectBody(ErrorResponse.class)
            .isEqualTo(new ErrorResponse(
                500,
                "Internal Server Error",
                "An internal server error occurred"
            ));
    }
}