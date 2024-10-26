package com.barzykin.mao.songservice.dto;

public record ErrorResponse(
    int status,
    String error,
    String message
) {}
