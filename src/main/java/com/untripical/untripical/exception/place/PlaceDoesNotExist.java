package com.untripical.untripical.exception.place;

public class PlaceDoesNotExist extends RuntimeException {
    public PlaceDoesNotExist(String message) {
        super(message);
    }
}
