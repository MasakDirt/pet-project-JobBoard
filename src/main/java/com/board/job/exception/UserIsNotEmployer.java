package com.board.job.exception;

public class UserIsNotEmployer extends RuntimeException{
    public UserIsNotEmployer(String message) {
        super(message);
    }
}
