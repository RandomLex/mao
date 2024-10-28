package com.barzykin.mao.songservice.services;

import com.barzykin.mao.songservice.errors.SongNotFoundException;
import com.barzykin.mao.songservice.model.Song;
import com.barzykin.mao.songservice.repositories.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class SongService {
    private final SongRepository songRepository;

    public Mono<Song> getSong(int id) {
        return songRepository.findById(id)
            .switchIfEmpty(Mono.error(new SongNotFoundException("Song with id " + id + " not found")));
    }

    public Mono<Integer> createSong(Song song) {
        return songRepository.save(song)
            .map(Song::id);
    }

    public Flux<Integer> deleteSongs(Collection<Integer> ids) {
        return songRepository.deleteSongsByIdIn(ids);
    }
}
