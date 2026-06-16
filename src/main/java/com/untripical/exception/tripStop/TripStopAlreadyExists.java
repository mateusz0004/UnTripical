package com.untripical.exception.tripStop;

public class TripStopAlreadyExists extends RuntimeException {
    public TripStopAlreadyExists(String message) {
        super(message);
    }
}
