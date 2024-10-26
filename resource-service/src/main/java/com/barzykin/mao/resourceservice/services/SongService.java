package com.barzykin.mao.resourceservice.services;

import lombok.RequiredArgsConstructor;
import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class SongService {
    private final WebClient.Builder webClientBuilder;
    private final Mp3Service mp3Service;

    public Mono<Void> postToSongService(Metadata metadata, long resourceId) {
        return webClientBuilder.build()
            .post()
            .uri("http://localhost:8082/songs")
            .bodyValue(mp3Service.toSongDto(metadata, resourceId))
            .retrieve()
            .bodyToMono(Void.class);
    }
}
