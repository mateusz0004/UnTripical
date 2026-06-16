package com.untripical.exception.place;

public class ListOfPlaceDoesNotExist extends RuntimeException {
    public ListOfPlaceDoesNotExist(String message) {
        super(message);
    }
}
