package com.barzykin.mao.songservice.repositories;

import com.barzykin.mao.songservice.model.Song;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.Collection;


@Repository
public interface SongRepository extends R2dbcRepository<Song, Integer> {

    @Query("DELETE FROM song WHERE id IN (:ids) RETURNING id")
    Flux<Integer> deleteSongsByIdIn(Collection<Integer> ids);
}
