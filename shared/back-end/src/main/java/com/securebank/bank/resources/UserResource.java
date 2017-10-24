package com.securebank.bank.resources;

import com.securebank.bank.model.User;
import com.securebank.bank.services.LoggedInService;
import com.securebank.bank.services.UserRepository;
import com.securebank.bank.services.ValidationService;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;
import javax.ws.rs.core.Response;
import com.securebank.bank.services.errors.ApplicationValidationError;
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

    @Autowired
    ValidationService validationService;

    @GET
    public List<User> getUsers(@HeaderParam("Authorization") String authorization) {
        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        // ensure that the user has permission to create an account for this user (the user themselves or tier1 or tier2)
        Map<String, Integer> roleLevel = new HashMap<String, Integer>();
        roleLevel.put("administrator", 3);
        roleLevel.put("tier2", 2);
        roleLevel.put("tier1", 1);
        roleLevel.put("external", 0);
        if (roleLevel.get(loggedInUser.getType()) < 1) {
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");
        }
        else if (roleLevel.get(loggedInUser.getType()) == 1) {
            List<User> list = userRepository.findAll();
            for (int i = 0; i < list.size(); i++) {
                if (roleLevel.get(list.get(i).getType()) > 1) {
                    list.remove(i);
                }
            }
            return list;
        }
        else if (roleLevel.get(loggedInUser.getType()) == 2){
            List<User> list = userRepository.findAll();
            for (int i = 0; i < list.size(); i++) {
                if (roleLevel.get(list.get(i).getType()) > 2) {
                    list.remove(i);
                }
            }
            return list;
        }
        else {
            return userRepository.findAll();
        }
    }

    @GET
    @Path("/{userId}")
    public User getUser(@PathParam("userId") String userId, @HeaderParam("Authorization") String authorization){
        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        // ensure that the user has permission to create an account for this user (the user themselves or tier1 or tier2)
        Map<String, Integer> roleLevel = new HashMap<String, Integer>();
        roleLevel.put("administrator", 3);
        roleLevel.put("tier2", 2);
        roleLevel.put("tier1", 1);
        roleLevel.put("external", 0);
        User oneUser = userRepository.findById(userId);
        if (roleLevel.get(loggedInUser.getType()) == 0) {
            if (loggedInUser.getId().equals(userId)) {
                return oneUser;
            }
            else
                throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");
        }
        else if (roleLevel.get(loggedInUser.getType()) > roleLevel.get(oneUser.getType()) ) {
            return oneUser;
        }
        else if (roleLevel.get(loggedInUser.getType()) == roleLevel.get(oneUser.getType()) && loggedInUser.getId().equals(oneUser.getId())) {
            return oneUser;
        }
        else throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");

    }

    @POST
    public User createUser(User user, @HeaderParam("Authorization") String authorization){
        String[] types = {"tier1", "tier2", "administrator", "external"};
        String userType = user.getType();
        if(Arrays.asList(types).contains(userType)) {
            if (user.getType().equals("external") || user.getType().equals("administrator")) {
                user.setId(null);// ensure that id is set by database
                return userRepository.save(user);
            }
            else if (user.getType().equals("tier1") || user.getType().equals("tier2")) {
                User loggedInUser = loggedInService.getLoggedInUser(authorization);
                if (loggedInUser.getType().equals("administrator")) {
                    user.setId(null);// ensure that id is set by database
                    return userRepository.save(user);
                }
            }
            else
                throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid auth");
        }
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid auth");

        return userRepository.save(user);
    }

    @PUT
    @Path("/{userId}")
    public User updateUser(@PathParam("userId") String userId, @HeaderParam("Authorization") String authorization, User user){
        User byId = userRepository.findById(userId);
        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        Map<String, Integer> roleLevel = new HashMap<String, Integer>();
        roleLevel.put("administrator", 3);
        roleLevel.put("tier2", 2);
        roleLevel.put("tier1", 1);
        roleLevel.put("external", 0);
        if( roleLevel.get(loggedInUser.getType()) == 0 || roleLevel.get(loggedInUser.getType()) <= roleLevel.get(byId.getType()) ) {
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");
        }
        else {
            BeanUtils.copyProperties(user, byId);
            return userRepository.save(byId);
        }
    }

    @DELETE
    @Path("/{userId}")
    public String deleteUser(@PathParam("userId") String userId, @HeaderParam("Authorization") String authorization){
        User byId = userRepository.findById(userId);
        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        Map<String, Integer> roleLevel = new HashMap<String, Integer>();
        roleLevel.put("administrator", 3);
        roleLevel.put("tier2", 2);
        roleLevel.put("tier1", 1);
        roleLevel.put("external", 0);
        if( roleLevel.get(loggedInUser.getType()) == 0)
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");
        else if (roleLevel.get(loggedInUser.getType()) == 2 && roleLevel.get(byId.getType()) == 0) {
            userRepository.deleteById(userId);
            return "{\"status\":\"success\"}";
        }
        else if (roleLevel.get(loggedInUser.getType()) == 3 && roleLevel.get(byId.getType()) < 3){
            userRepository.deleteById(userId);
            return "{\"status\":\"success\"}";
        }
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");
    }
}
