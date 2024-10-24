package com.barzykin.mao.songservice.endpoints;

public record ErrorResponse(
    int status,
    String error,
    String message
) {}
