package com.barzykin.mao.resourceservice.endpoints;

import com.barzykin.mao.resourceservice.dto.ErrorResponse;
import com.barzykin.mao.resourceservice.services.FilePartService;
import com.barzykin.mao.resourceservice.services.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.List;

@WebFluxTest(controllers = ResourceEndpoint.class)
class ResourceEndpointDeleteTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ResourceService resourceService;

    @MockBean
    private FilePartService filePartService;

    @BeforeEach
    void setUp() {
        Mockito.reset(resourceService, filePartService);
    }

    @Test
    void deleteResources_all_present_success() {
        Mockito.when(resourceService.deleteResources(List.of(1L, 2L))).thenReturn(Flux.just(1L, 2L));

        webTestClient.delete()
            .uri("/resources?ids=1,2")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Long.class)
            .value(response -> {
                assert response.size() == 2;
                assert response.contains(1L);
                assert response.contains(2L);
            });
    }

    @Test
    void deleteResources_all_absent_success() {
        Mockito.when(resourceService.deleteResources(List.of(1L, 2L))).thenReturn(Flux.empty());

        webTestClient.delete()
            .uri("/resources?ids=1,2")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Long.class)
            .value(response -> {
                assert response.isEmpty();
            });
    }

    @Test
    void deleteResources_internalServerError() {
        Mockito.when(resourceService.deleteResources(List.of(1L, 2L))).thenReturn(Flux.error(new RuntimeException("Internal server error")));

        webTestClient.delete()
            .uri("/resources?ids=1,2")
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
