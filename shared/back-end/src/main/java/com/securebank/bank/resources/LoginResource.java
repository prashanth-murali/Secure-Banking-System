package com.securebank.bank.resources;

import com.securebank.bank.model.Login;
import com.securebank.bank.model.User;
import com.securebank.bank.services.UserRepository;
import com.securebank.bank.services.errors.ApplicationValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import com.securebank.bank.services.LoggedInService;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.security.MessageDigest;
import java.util.*;
import java.util.Date;
import javax.ws.rs.core.MediaType;

@Component
@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginResource {

    @Autowired
    UserRepository userRepository;

    @POST
    public Response login(Login login){
        User byUsername = userRepository.findByUsername(login.getUsername());

        if(byUsername == null){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        if(login.getPassword().equals(byUsername.getPassword())){
            return Response.status(Response.Status.OK).entity(byUsername).type(MediaType.APPLICATION_JSON).build();
        }else{
            // invalid password
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

}
