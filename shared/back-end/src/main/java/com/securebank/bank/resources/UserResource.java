package com.securebank.bank.resources;

import com.securebank.bank.model.Account;
import com.securebank.bank.model.User;
import com.securebank.bank.services.LoggedInService;
import com.securebank.bank.services.UserRepository;
import com.securebank.bank.services.errors.ApplicationValidationError;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Component
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    Logger logger = Logger.getLogger(UserRepository.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    LoggedInService loggedInService;

    @GET
    public List<User> getUsers(@HeaderParam("Authorization") String authorization) {
        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        if(!loggedInUser.getType().equals("administrator")){
            logger.error("getUser call rejected, must be administrator user type was: " +loggedInUser.getType());
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");
        }else{
            return userRepository.findAll();
        }
    }

    @GET
    @Path("/{userId}")
    public User getUser(@PathParam("userId") String userId, @HeaderParam("Authorization") String authorization){


        return userRepository.findById(userId);
    }

    @POST
    public User createUser(User user){
        user.setId(null);// ensure the user does not pass their own id to mongo
        user.setAccounts(new ArrayList<Account>()); // set to empty list
        return userRepository.save(user);
    }

    @PUT
    @Path("/{userId}")
    public User updateUser(@PathParam("userId") String userId, User user){
        User byId = userRepository.findById(userId);
        BeanUtils.copyProperties(user, byId);

        if(byId.getAccounts() == null){ // set to empty list if property is not htere
            user.setAccounts(new ArrayList<Account>());
        }

        return userRepository.save(byId);
    }

    @DELETE
    @Path("/{userId}")
    public String deleteUser(@PathParam("userId") String userId){
        userRepository.deleteById(userId);
        return "{\"status\":\"success\"}";
    }

    @POST
    @Path("/{userId}/accounts/")
    public Response createAccountForUser(@PathParam("userId") String userId, Account account){
        User user = userRepository.findById(userId);
        if(user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();// user not found
        }

        account.setId(new ObjectId().toString());
        if(user.getAccounts() == null){
            user.setAccounts(new ArrayList<>());
        }

        return Response.status(Response.Status.OK).entity(user).build();
    }

}
