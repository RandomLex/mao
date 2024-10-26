package com.barzykin.mao.resourceservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SongDto(
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name should not exceed 255 characters")
    String name,
    @NotBlank(message = "Artist is required")
    @Size(max = 255, message = "Artist name should not exceed 255 characters")
    String artist,
    @Size(max = 255, message = "Album name should not exceed 255 characters")
    String album,
    @Size(max = 10, message = "Length should not exceed 10 characters")
    String length,
    long resourceId,
    @Size(max = 4, message = "Year should not exceed 4 characters")
    String year
) {
}
