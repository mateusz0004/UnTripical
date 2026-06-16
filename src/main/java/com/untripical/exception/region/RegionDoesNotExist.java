package com.untripical.exception.region;

public class RegionDoesNotExist extends RuntimeException {
    public RegionDoesNotExist(String message) {
        super(message);
    }
}
