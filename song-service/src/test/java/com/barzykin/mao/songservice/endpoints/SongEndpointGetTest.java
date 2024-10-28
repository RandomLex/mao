package com.barzykin.mao.songservice.endpoints;

import com.barzykin.mao.songservice.congurations.MapperConfig;
import com.barzykin.mao.songservice.dto.ErrorResponse;
import com.barzykin.mao.songservice.dto.SongDto;

import com.barzykin.mao.songservice.errors.SongNotFoundException;
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
class SongEndpointGetTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SongService songService;

    @BeforeEach
    void setUp() {
        Mockito.reset(songService);
    }

    @Test
    void getSong_success() {
        Song mockSong = new Song(
            1,
            "Bohemian Rhapsody",
            "Queen",
            "A Night at the Opera",
            "5:55",
            12345L,
            "1975");

        Mockito.when(songService.getSong(1)).thenReturn(Mono.just(mockSong));

        webTestClient.get()
            .uri("/songs/1")
            .exchange()
            .expectStatus().isOk()
            .expectBody(SongDto.class)
            .isEqualTo(new SongDto(
                "Bohemian Rhapsody",
                "Queen",
                "A Night at the Opera",
                "5:55",
                12345L,
                "1975"
            ));
    }

    @Test
    void getSong_notFound() {
        Mockito.when(songService.getSong(1)).thenThrow(new SongNotFoundException("Song with id 1 not found"));
        webTestClient.get()
            .uri("/songs/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(ErrorResponse.class)
            .isEqualTo(new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                "Song with id 1 not found"
            ));
    }

    @Test
    void getSong_internalServerError() {
        Mockito.when(songService.getSong(1)).thenReturn(Mono.error(new RuntimeException("Internal server error")));
        webTestClient.get()
            .uri("/songs/1")
            .accept(MediaType.APPLICATION_JSON)
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