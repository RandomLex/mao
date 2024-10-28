package com.barzykin.mao.songservice.endpoints;

import com.barzykin.mao.songservice.dto.SongDeleteResponse;
import com.barzykin.mao.songservice.dto.SongDto;
import com.barzykin.mao.songservice.dto.SongCreateResponse;
import com.barzykin.mao.songservice.model.Song;
import com.barzykin.mao.songservice.services.SongService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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

@RestController
@RequiredArgsConstructor
@Slf4j
public class SongEndpoint {
    private final SongService songService;
    private final ModelMapper modelMapper;

    @GetMapping("/songs/{id}")
    public Mono<ResponseEntity<SongDto>> getSong(@PathVariable int id) {
        return songService.getSong(id)
            .map(song -> ResponseEntity.ok(modelMapper.map(song, SongDto.class)));
    }

    @PostMapping("/songs")
    public Mono<ResponseEntity<SongCreateResponse>> createSong(@Valid @RequestBody SongDto request) {
        log.info("Creating song: {}", request);
        // Error handling via GlobalExceptionHandler by for Non-valid responses
        return songService.createSong(modelMapper.map(request, Song.class))
            .map(id -> ResponseEntity.ok(new SongCreateResponse(id)));
    }

    @Validated
    @DeleteMapping("/songs")
    public Mono<ResponseEntity<SongDeleteResponse>> deleteSongs(
        @RequestParam(value = "id", required = false)
        @Size(max = 200-1, message = "Parameter 'id' length must be less than 200 characters.") String ids) {
        // Error handling via GlobalExceptionHandler by default
        return songService.deleteSongs(strToIntegers(ids))
            .collectList()
            .map(deletedIds -> ResponseEntity.ok(new SongDeleteResponse(deletedIds.stream().mapToInt(i -> i).toArray())));
    }

    private static List<Integer> strToIntegers(String ids) {
        if (ids == null) {
            return List.of();
        }
        return Arrays.stream(ids.split(","))
            .map(Integer::parseInt)
            .toList();
    }
}
