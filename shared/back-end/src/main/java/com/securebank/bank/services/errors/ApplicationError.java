package com.securebank.bank.services.errors;

public class ApplicationError {
    private String error;

    public ApplicationError(String error) {
        this.error = error;
    }

    public ApplicationError() {
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
