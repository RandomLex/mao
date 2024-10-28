package com.barzykin.mao.resourceservice.endpoints;

import com.barzykin.mao.resourceservice.dto.SongCreateResponse;
import com.barzykin.mao.resourceservice.services.FilePartService;
import com.barzykin.mao.resourceservice.services.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ResourceEndpoint {
    private final FilePartService filePartService;
    private final ResourceService resourceService;

    @PostMapping(value = "/resources", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<?>> uploadMp3(@RequestPart("file") Mono<FilePart> filePart) {
        return filePart
            .flatMap(filePartService::extractBytesFromFilePart)
            .flatMap(resourceService::validateMp3File)
            .flatMap(resourceService::saveResourceAndPostToSongService)
            .map(savedResourceId -> ResponseEntity.ok(new SongCreateResponse(savedResourceId)));
    }

    @GetMapping(value = "/resources/{id}")
    public Mono<ResponseEntity<byte[]>> getResourceById(@PathVariable Long id) {
        return resourceService.getResourceById(id)
            .map(bytes -> ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"song.mp3\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes));
    }

    @DeleteMapping("/resources")
    public Flux<Long> deleteSongs(@RequestParam(value = "id", required = false) String ids) {
        // Error handling via GlobalExceptionHandler by default
        return resourceService.deleteResources(strToLongs(ids));
    }

    private static List<Long> strToLongs(String ids) {
        if (ids == null) {
            return List.of();
        }
        return Arrays.stream(ids.split(","))
            .map(Long::parseLong)
            .toList();
    }
}
