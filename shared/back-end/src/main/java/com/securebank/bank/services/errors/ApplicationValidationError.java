package com.securebank.bank.services.errors;

import javax.ws.rs.core.Response;

public class ApplicationValidationError extends RuntimeException {

    Response.Status status;

    public ApplicationValidationError(Response.Status status, String message) {
        super(message);

        this.status = status;
    }

    public Response.Status getStatus() {
        return status;
    }

    public void setStatus(Response.Status status) {
        this.status = status;
    }
}
