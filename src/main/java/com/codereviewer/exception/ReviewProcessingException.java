package com.codereviewer.exception;

public class ReviewProcessingException extends RuntimeException {
    public ReviewProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}