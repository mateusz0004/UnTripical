package com.untripical.exception.tripPlan;

public class TripPlanDoesNotExist extends RuntimeException {
    public TripPlanDoesNotExist(String message) {
        super(message);
    }
}
