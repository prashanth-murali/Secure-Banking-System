package com.securebank.bank.services.errors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GlobalErrorMapper implements ExceptionMapper<Throwable> {
	private Logger logger = LoggerFactory.getLogger(GlobalErrorMapper.class);

	@Override
	public Response toResponse(Throwable throwable) {
	    logger.error("global error handler", throwable);
		ApplicationError applicationError = new ApplicationError("no details");
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(applicationError).type(MediaType.APPLICATION_JSON).build(); }
}
