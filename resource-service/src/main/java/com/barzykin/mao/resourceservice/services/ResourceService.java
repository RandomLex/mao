package com.barzykin.mao.resourceservice.services;

import com.barzykin.mao.resourceservice.exceptions.InvalidFileException;
import com.barzykin.mao.resourceservice.exceptions.ResourceNotFoundException;
import com.barzykin.mao.resourceservice.model.Resource;
import com.barzykin.mao.resourceservice.repositories.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final Mp3Service mp3Service;
    private final SongService songService;

    public Mono<byte[]> validateMp3File(byte[] bytes) {
        return mp3Service.isMp3(bytes)
            .flatMap(isMp3 -> {
                if (!isMp3) {
                    return Mono.error(new InvalidFileException("File is not an MP3"));
                }
                return mp3Service.validateMp3Structure(bytes)
                    .flatMap(isValid -> {
                        if (!isValid) {
                            return Mono.error(new InvalidFileException("Invalid MP3 file structure"));
                        }
                        return Mono.just(bytes);
                    });
            });
    }

    public Mono<Long> saveResourceAndPostToSongService(byte[] mp3Bytes) {
        return mp3Service.extractMetadata(mp3Bytes)
            .flatMap(metadata -> resourceRepository.saveAndGetId(new Resource(mp3Bytes))
                .flatMap(savedResource -> songService.postToSongService(metadata, savedResource.id())
                    .thenReturn(savedResource.id())
                )
            );
    }

    public Mono<byte[]> getResourceById(Long id) {
        return resourceRepository.findById(id)
            .map(Resource::data)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Resource not found")));
    }


    public Flux<Long> deleteResources(Iterable<Long> ids) {
        return resourceRepository.deleteAllByIdIn(ids);
    }
}
