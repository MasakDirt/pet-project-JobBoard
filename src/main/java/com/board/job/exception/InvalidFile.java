package com.board.job.exception;

public class InvalidFile extends RuntimeException {
    public InvalidFile() {
    }

    public InvalidFile(String message) {
        super(message);
    }
}
