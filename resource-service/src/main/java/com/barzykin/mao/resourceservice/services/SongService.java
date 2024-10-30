package com.barzykin.mao.resourceservice.services;

import lombok.RequiredArgsConstructor;
import org.apache.tika.metadata.Metadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class SongService {
    @Value("${song-service.url}")
    private String songServiceUrl;

    private final WebClient.Builder webClientBuilder;
    private final Mp3Service mp3Service;

    public Mono<Void> postToSongService(Metadata metadata, int resourceId) {
        return webClientBuilder.build()
            .post()
            .uri(songServiceUrl + "/songs")
            .bodyValue(mp3Service.toSongDto(metadata, resourceId))
            .retrieve()
            .bodyToMono(Void.class);
    }
}
