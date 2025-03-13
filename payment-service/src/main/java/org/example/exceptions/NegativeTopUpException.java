package org.example.exceptions;

public class NegativeTopUpException extends Exception{
    public NegativeTopUpException(String message) {
        super(message);
    }
}
