package com.barzykin.mao.songservice.endpoints;

import com.barzykin.mao.songservice.congurations.MapperConfig;
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
        Mockito.when(songService.deleteSongs(List.of(1L, 2L))).thenReturn(Flux.just(1L, 2L));
        webTestClient.delete()
            .uri("/songs?ids=1,2")
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
    void deleteSongs_all_absent_success() {
        Mockito.when(songService.deleteSongs(List.of(1L, 2L))).thenReturn(Flux.empty());
        webTestClient.delete()
            .uri("/songs?ids=1,2")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Long.class)
            .value(response -> {
                assert response.isEmpty();
            });
    }

    @Test
    void deleteSong_internalServerError() {
        Mockito.when(songService.deleteSongs(List.of(1L, 2L))).thenReturn(Flux.error(new RuntimeException("Internal server error")));
        webTestClient.delete()
            .uri("/songs?ids=1,2")
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