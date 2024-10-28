package com.barzykin.mao.songservice.endpoints;

import com.barzykin.mao.songservice.congurations.MapperConfig;
import com.barzykin.mao.songservice.dto.ErrorResponse;
import com.barzykin.mao.songservice.dto.SongDeleteResponse;
import com.barzykin.mao.songservice.services.SongService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.List;

@WebFluxTest(controllers = SongEndpoint.class)
@Import(MapperConfig.class)
class SongEndpointDeleteTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SongService songService;

    @BeforeEach
    void setUp() {
        Mockito.reset(songService);
    }

    @Test
    void deleteSongs_all_present_success() {
        Mockito.when(songService.deleteSongs(List.of(1, 2))).thenReturn(Flux.just(1, 2));
        webTestClient.delete()
            .uri("/songs?id=1,2")
            .exchange()
            .expectStatus().isOk()
            .expectBody(SongDeleteResponse.class)
            .value(response -> {
                assert response.ids().length == 2;
                assert response.ids()[0] == 1;
                assert response.ids()[1] == 2;
            });
    }

    @Test
    void deleteSongs_all_absent_success() {
        Mockito.when(songService.deleteSongs(List.of(1, 2))).thenReturn(Flux.empty());
        webTestClient.delete()
            .uri("/songs?id=1,2")
            .exchange()
            .expectStatus().isOk()
            .expectBody(SongDeleteResponse.class)
            .value(response -> {
                assert response.ids().length == 0;
            });
    }

    @Test
    void deleteSong_internalServerError() {
        Mockito.when(songService.deleteSongs(List.of(1, 2))).thenReturn(Flux.error(new RuntimeException("Internal server error")));
        webTestClient.delete()
            .uri("/songs?id=1,2")
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
    void deleteSongs_lengthOfIdParameterIsMoreThan199Symbols() {
        webTestClient.delete()
            .uri("/songs?id=" + "x".repeat(200))
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