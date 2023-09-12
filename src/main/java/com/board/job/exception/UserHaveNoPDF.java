package com.board.job.exception;

public class UserHaveNoPDF extends RuntimeException{
    public UserHaveNoPDF() {
    }

    public UserHaveNoPDF(String message) {
        super(message);
    }
}
