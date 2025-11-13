package com.untripical.exception.user;

public class IncorrectRoleTypeException extends RuntimeException {
    public IncorrectRoleTypeException(String message) {
        super(message);
    }
}
