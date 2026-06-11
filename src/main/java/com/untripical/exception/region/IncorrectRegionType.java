package com.untripical.exception.region;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class IncorrectRegionType extends RuntimeException {
    public IncorrectRegionType(String message) {
        super(message);
    }
}
