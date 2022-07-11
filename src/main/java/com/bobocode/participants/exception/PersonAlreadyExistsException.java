package com.bobocode.participants.exception;

public class PersonAlreadyExistsException extends RuntimeException {

    public PersonAlreadyExistsException() {
        super("A person with such ip address already exists");
    }
}
