package com.untripical.exception.review;

public class ReviewDoesNotExist extends RuntimeException {
    public ReviewDoesNotExist(String message) {
        super(message);
    }
}
