package com.untripical.exception.guideDetails;

public class GuideDetailsDoesNotExist extends RuntimeException {
    public GuideDetailsDoesNotExist(String message) {
        super(message);
    }
}
