package com.barzykin.mao.songservice.model;

import org.springframework.data.annotation.Id;

public record Song(
    @Id
    Integer id,
    String name,
    String artist,
    String album,
    String length,
    long resourceId,
    String year
) {}
