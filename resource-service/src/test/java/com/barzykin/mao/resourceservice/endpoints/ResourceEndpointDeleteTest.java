package com.barzykin.mao.resourceservice.endpoints;

import com.barzykin.mao.resourceservice.dto.ErrorResponse;
import com.barzykin.mao.resourceservice.dto.ResourceDeleteResponse;
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

    @BeforeEach
    void setUp() {
        Mockito.reset(resourceService);
    }

    @Test
    void deleteResources_all_present_success() {
        Mockito.when(resourceService.deleteResources(List.of(1, 2))).thenReturn(Flux.just(1, 2));

        webTestClient.delete()
            .uri("/resources?id=1,2")
            .exchange()
            .expectStatus().isOk()
            .expectBody(ResourceDeleteResponse.class)
            .value(response -> {
                assert response.ids().length == 2;
                assert response.ids()[0] == 1;
                assert response.ids()[1] == 2;
            });
    }

    @Test
    void deleteResources_all_absent_success() {
        Mockito.when(resourceService.deleteResources(List.of(1, 2))).thenReturn(Flux.empty());

        webTestClient.delete()
            .uri("/resources?id=1,2")
            .exchange()
            .expectStatus().isOk()
            .expectBody(ResourceDeleteResponse.class)
            .value(response -> {
                assert response.ids().length == 0;
            });
    }

    @Test
    void deleteResources_internalServerError() {
        Mockito.when(resourceService.deleteResources(List.of(1, 2))).thenReturn(Flux.error(new RuntimeException("Internal server error")));

        webTestClient.delete()
            .uri("/resources?id=1,2")
            .exchange()
            .expectStatus().isEqualTo(500)
            .expectBody(ErrorResponse.class)
            .isEqualTo(new ErrorResponse(
                500,
                "Internal Server Error",
                "An internal server error occurred"
            ));
    }

    @Test
    void deleteResources_lengthOfIdParameterIsMoreThan199Symbols() {
        webTestClient.delete()
            .uri("/resources?id=" + "x".repeat(200))
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(ErrorResponse.class)
            .isEqualTo(new ErrorResponse(
                400,
                "Bad Request",
                "400 BAD_REQUEST \"Validation failure\""
            ));
    }

}
