package com.barzykin.mao.resourceservice.exceptions;

public class InvalidIdSequenceException extends RuntimeException {

    public InvalidIdSequenceException() {
        super("Each element of Id sequence must be a positive number.");
    }

    public InvalidIdSequenceException(String message) {
        super(message);
    }
}
