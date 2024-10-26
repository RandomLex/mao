package com.barzykin.mao.resourceservice.dto;

public record ErrorResponse(
    int status,
    String error,
    String message
) {}
