package com.securebank.bank.services.errors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ApplicationValidationErrorMapper implements ExceptionMapper<ApplicationValidationError> {
	private Logger logger = LoggerFactory.getLogger(ApplicationValidationErrorMapper.class);

	@Override
	public Response toResponse(ApplicationValidationError applicationValidationError) {
		ApplicationError applicationError = new ApplicationError(applicationValidationError.getMessage());
		return Response.status(applicationValidationError.getStatus()).entity(applicationError).type(MediaType.APPLICATION_JSON).build();
	}
}
