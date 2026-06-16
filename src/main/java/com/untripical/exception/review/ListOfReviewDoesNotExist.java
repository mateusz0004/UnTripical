package com.untripical.exception.review;

public class ListOfReviewDoesNotExist extends RuntimeException {
    public ListOfReviewDoesNotExist(String message) {
        super(message);
    }
}
