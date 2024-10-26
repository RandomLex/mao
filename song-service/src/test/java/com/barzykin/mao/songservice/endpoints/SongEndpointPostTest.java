package com.barzykin.mao.songservice.endpoints;

import com.barzykin.mao.songservice.congurations.MapperConfig;
import com.barzykin.mao.songservice.dto.ErrorResponse;
import com.barzykin.mao.songservice.dto.SongCreateResponse;
import com.barzykin.mao.songservice.dto.SongDto;
import com.barzykin.mao.songservice.model.Song;
import com.barzykin.mao.songservice.services.SongService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = SongEndpoint.class)
@Import(MapperConfig.class)
class SongEndpointPostTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SongService songService;

    @BeforeEach
    void setUp() {
        Mockito.reset(songService);
    }

    @Test
    void createSong_success() {
        SongDto request = new SongDto(
            "Bohemian Rhapsody",
            "Queen",
            "A Night at the Opera",
            "5:55",
            12345L,
            "1975");

        Mockito.when(songService.createSong(Mockito.any(Song.class))).thenReturn(Mono.just(1L));

        webTestClient.post()
            .uri("/songs")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk()
            .expectBody(SongCreateResponse.class)
            .value(response -> {
                assert response.id() == 1L;
            });
    }

    @Test
    void createSong_badRequest() {
        SongDto invalidRequest = new SongDto("", "Queen", "A Night at the Opera", "5:55", 12345L, "1975");

        webTestClient.post()
            .uri("/songs")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequest)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(ErrorResponse.class)
            .isEqualTo(new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Name is required"
            ));
    }

    @Test
    void createSong_internalServerError() {
        SongDto request = new SongDto("Bohemian Rhapsody", "Queen", "A Night at the Opera", "5:55", 12345L, "1975");

        Mockito.when(songService.createSong(Mockito.any(Song.class))).thenReturn(Mono.error(new RuntimeException("Internal server error")));
        webTestClient.post()
            .uri("/songs")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
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