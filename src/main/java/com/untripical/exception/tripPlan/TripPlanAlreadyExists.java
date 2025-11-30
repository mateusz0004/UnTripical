package com.untripical.exception.tripPlan;

public class TripPlanAlreadyExists extends RuntimeException {
    public TripPlanAlreadyExists(String message) {
        super(message);
    }
}
