package com.barzykin.mao.resourceservice.endpoints;

import com.barzykin.mao.resourceservice.dto.ErrorResponse;
import com.barzykin.mao.resourceservice.exceptions.ResourceNotFoundException;
import com.barzykin.mao.resourceservice.services.FilePartService;
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
class ResourceEndpointGetTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ResourceService resourceService;

    @MockBean
    FilePartService filePartService;

    @BeforeEach
    void setUp() {
        Mockito.reset(resourceService, filePartService);
    }

    @Test
    void getResourceById_success() {
        byte[] audioData = new byte[]{/* audio data bytes */};
        Mockito.when(resourceService.getResourceById(1L)).thenReturn(Mono.just(audioData));

        webTestClient.get()
            .uri("/resources/1")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_OCTET_STREAM)
            .expectBody(byte[].class)
            .isEqualTo(audioData);
    }

    @Test
    void getResourceById_notFound() {
        Mockito.when(resourceService.getResourceById(1L)).thenThrow(new ResourceNotFoundException("Resource with id 1 not found"));

        webTestClient.get()
            .uri("/resources/1")
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(ErrorResponse.class)
            .isEqualTo(new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                "Resource with id 1 not found"
            ));
    }

    @Test
    void getResourceById_internalServerError() {
        Mockito.when(resourceService.getResourceById(1L)).thenReturn(Mono.error(new RuntimeException("Internal server error")));

        webTestClient.get()
            .uri("/resources/1")
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
