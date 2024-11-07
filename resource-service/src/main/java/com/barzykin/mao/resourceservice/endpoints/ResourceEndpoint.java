package com.barzykin.mao.resourceservice.endpoints;

import com.barzykin.mao.resourceservice.dto.ResourceDeleteResponse;
import com.barzykin.mao.resourceservice.dto.SongCreateResponse;
import com.barzykin.mao.resourceservice.exceptions.InvalidIdSequenceException;
import com.barzykin.mao.resourceservice.services.ResourceService;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ResourceEndpoint {
    private final ResourceService resourceService;

    @PostMapping(value = "/resources", consumes = "audio/mpeg")
    public Mono<ResponseEntity<?>> uploadMp3(@RequestBody Mono<byte[]> audioData) {
        return audioData
            .flatMap(resourceService::validateMp3Data)
            .flatMap(resourceService::saveResource)
            .flatMap(resourceService::extractMetadataAndPostToSongService)
            .map(savedResourceId -> ResponseEntity.ok(new SongCreateResponse(savedResourceId)));
    }

    @GetMapping(value = "/resources/{id}")
    public Mono<ResponseEntity<byte[]>> getResourceById(@Positive @PathVariable Integer id) {
        return resourceService.getResourceById(id)
            .map(bytes -> ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"song.mp3\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes));
    }

    @Validated
    @DeleteMapping("/resources")
    public Mono<ResponseEntity<ResourceDeleteResponse>> deleteSongs(
        @RequestParam(value = "id", required = false)
        @Size(max = 200-1, message = "Parameter 'id' length must be less than 200 characters.") String ids) {
        // Error handling via GlobalExceptionHandler by default
        return resourceService.deleteResources(validateAndParseIds(ids))
            .collectList()
            .map(deletedIds -> ResponseEntity.ok(new ResourceDeleteResponse(deletedIds.stream().mapToInt(i -> i).toArray())));
    }

    private static List<Integer> validateAndParseIds(String ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        try {
            return Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .peek(id -> {
                    if (id <= 0) {
                        throw new InvalidIdSequenceException();
                    }
                })
                .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new InvalidIdSequenceException();
        }
    }

}
