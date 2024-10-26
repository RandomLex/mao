package com.barzykin.mao.songservice.errors;

public class SongNotFoundException extends RuntimeException{
    public SongNotFoundException(String failedToSaveSong) {
        super(failedToSaveSong);
    }
}
