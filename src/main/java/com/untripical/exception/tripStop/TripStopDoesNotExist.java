package com.untripical.exception.tripStop;

public class TripStopDoesNotExist extends RuntimeException {
    public TripStopDoesNotExist(String message) {
        super(message);
    }
}
