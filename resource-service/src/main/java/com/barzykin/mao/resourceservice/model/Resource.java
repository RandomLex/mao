package com.barzykin.mao.resourceservice.model;

import org.springframework.data.annotation.Id;

public record Resource(
    @Id
    Long id,
    byte[] data
) {
    public Resource(byte[] data) {
        this(null, data);
    }
}
