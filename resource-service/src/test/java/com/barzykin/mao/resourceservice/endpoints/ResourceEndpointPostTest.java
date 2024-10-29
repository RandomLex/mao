package com.barzykin.mao.resourceservice.endpoints;

import com.barzykin.mao.resourceservice.dto.ErrorResponse;
import com.barzykin.mao.resourceservice.dto.SongCreateResponse;
import com.barzykin.mao.resourceservice.exceptions.InvalidFileException;
import com.barzykin.mao.resourceservice.model.Resource;
import com.barzykin.mao.resourceservice.services.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = ResourceEndpoint.class)
class ResourceEndpointPostTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ResourceService resourceService;

    @BeforeEach
    void setUp() {
        Mockito.reset(resourceService);
    }

    @Test
    void uploadMp3_success() {
        byte[] validMp3Data = new byte[]{/* mp3 data bytes */};
        Resource savedResource = new Resource(1, validMp3Data);

        Mockito.when(resourceService.validateMp3Data(validMp3Data)).thenReturn(Mono.just(validMp3Data));
        Mockito.when(resourceService.saveResource(validMp3Data)).thenReturn(Mono.just(savedResource));
        Mockito.when(resourceService.extractMetadataAndPostToSongService(savedResource)).thenReturn(Mono.just(1));

        webTestClient.post()
            .uri("/resources")
            .contentType(MediaType.parseMediaType("audio/mpeg"))
            .bodyValue(validMp3Data)
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
        Mockito.when(resourceService.validateMp3Data(invalidMp3Data))
            .thenReturn(Mono.error(new InvalidFileException("File is not a valid MP3")));

        webTestClient.post()
            .uri("/resources")
            .contentType(MediaType.parseMediaType("audio/mpeg"))
            .bodyValue(invalidMp3Data)
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
        Mockito.when(resourceService.validateMp3Data(validMp3Data)).thenReturn(Mono.just(validMp3Data));
        Mockito.when(resourceService.saveResource(validMp3Data)).thenReturn(Mono.error(new RuntimeException("Internal server error")));

        webTestClient.post()
            .uri("/resources")
            .contentType(MediaType.parseMediaType("audio/mpeg"))
            .bodyValue(validMp3Data)
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
