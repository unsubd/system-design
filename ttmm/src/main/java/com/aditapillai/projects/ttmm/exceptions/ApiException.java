package com.aditapillai.projects.ttmm.exceptions;

public class ApiException extends RuntimeException {

    private final String message;
    private final int status;

    public ApiException(int status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
