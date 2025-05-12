package it.globus.finance.configuration.exception;

public class RequestException extends RuntimeException {
    public RequestException(String message) {
        super(message);
    }
}
