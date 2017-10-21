package com.securebank.bank.resources;

import com.securebank.bank.model.Account;
import com.securebank.bank.model.User;
import com.securebank.bank.services.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Autowired
    UserRepository userRepository;

    @GET
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @GET
    @Path("/{userId}")
    public User getUser(@PathParam("userId") String userId){
        return userRepository.findById(userId);
    }

    @POST
    public User createUser(User user){
        user.setId(null);// ensure the user does not pass their own id to mongo
        return userRepository.save(user);
    }

    @PUT
    @Path("/{userId}")
    public User updateUser(@PathParam("userId") String userId, User user){
        User byId = userRepository.findById(userId);
        BeanUtils.copyProperties(user, byId);
        return userRepository.save(byId);
    }

    @DELETE
    @Path("/{userId}")
    public String deleteUser(@PathParam("userId") String userId){
        userRepository.deleteById(userId);
        return "{\"status\":\"success\"}";
    }

    @POST
    @Path("/account/{userId}")
    public void createAccount(@PathParam("userId") String userId, Account account){
        User user = userRepository.findById(userId);
    }

}
