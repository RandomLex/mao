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

    public Mono<byte[]> validateMp3Data(byte[] bytes) {
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

    public Mono<Resource> saveResource(byte[] mp3Bytes) {
        return resourceRepository.saveAndGetId(new Resource(mp3Bytes));
    }

    public Mono<Integer> extractMetadataAndPostToSongService(Resource resource) {
        return mp3Service.extractMetadata(resource.data())
            .flatMap(metadata -> songService.postToSongService(metadata, resource.id())
                .thenReturn(resource.id()));
    }

    public Mono<byte[]> getResourceById(Integer id) {
        return resourceRepository.findById(id)
            .map(Resource::data)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Resource not found")));
    }


    public Flux<Integer> deleteResources(Iterable<Integer> ids) {
        return resourceRepository.deleteAllByIdIn(ids);
    }
}
