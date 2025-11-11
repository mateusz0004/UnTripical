package com.untripical.exception.guideAnnouncementTable;

public class GuideAnnouncementTableDoesNotExist extends RuntimeException {
    public GuideAnnouncementTableDoesNotExist(String message) {
        super(message);
    }
}
