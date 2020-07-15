package com.mx.daac.exceptions;

public class PostNotFoundException extends NullPointerException {
    public PostNotFoundException(String id) {
        super(id);
    }
}
