package com.barzykin.mao.resourceservice.repositories;

import com.barzykin.mao.resourceservice.model.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ResourceRepository extends R2dbcRepository<Resource, Long> {
    @Query("INSERT INTO resource (data) VALUES (:#{#resource.data}) RETURNING *")
    Mono<Resource> saveAndGetId(@NotNull Resource resource);

    @Query("DELETE FROM resource WHERE id IN (:ids) RETURNING id")
    Flux<Long> deleteAllByIdIn(Iterable<Long> ids);
}
