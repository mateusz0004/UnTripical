package com.untripical.exception.place;

public class PlaceWithNameIsExisting extends RuntimeException {
    public PlaceWithNameIsExisting(String message) {
        super(message);
    }
}
