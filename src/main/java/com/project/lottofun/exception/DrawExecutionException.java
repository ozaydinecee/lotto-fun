package com.project.lottofun.exception;

public class DrawExecutionException extends RuntimeException {
    public DrawExecutionException(String message) {
        super(message);
    }

    public DrawExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}