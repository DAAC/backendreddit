package com.mx.daac.exceptions;

public class SubredditNotFoundException extends NullPointerException{
    public SubredditNotFoundException(String subredditName) {
        super(subredditName);
    }
}
