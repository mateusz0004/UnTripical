package com.untripical.exception.announcement;

public class AnnouncementAlreadyExists extends RuntimeException {
    public AnnouncementAlreadyExists(String message) {
        super(message);
    }
}
