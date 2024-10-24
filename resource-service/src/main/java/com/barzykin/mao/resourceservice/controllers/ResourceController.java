package com.barzykin.mao.resourceservice.controllers;

import com.barzykin.mao.resourceservice.services.Mp3Handler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ResourceController {
    private final Mp3Handler mp3Handler;

    @GetMapping("/resource")
    public Mono<String> getResource() {
        mp3Handler.handleMp3("queen.mp3");
        return Mono.just("Resource");
    }
}
