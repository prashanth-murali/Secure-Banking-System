package com.securebank.bank.services;

import com.securebank.bank.model.User;
import com.securebank.bank.services.errors.ApplicationValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;

@Component
public class ValidationService {
    Logger logger = LoggerFactory.getLogger(ValidationService.class);

    public void validateLoggedInUserIsAdmin(User loggedInUser){
        if(!loggedInUser.getType().equals("administrator")){
            logger.error("getUser call rejected, must be administrator user type was: " +loggedInUser.getType());
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");
        }
    }
}
