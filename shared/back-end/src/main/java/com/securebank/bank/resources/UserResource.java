package com.securebank.bank.resources;

import com.securebank.bank.model.User;
import com.securebank.bank.services.LoggedInService;
import com.securebank.bank.services.UserRepository;
import com.securebank.bank.services.ValidationService;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
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
        roleLevel.put("merchant", 0);
        roleLevel.put("consumer", 0);
        if (roleLevel.get(loggedInUser.getType()) == 0) {
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");
        }
        else if (roleLevel.get(loggedInUser.getType()) == 1 && loggedInUser.getAuthorization().equals("true")) {
            List<User> list = userRepository.findAll();
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setPassword(null);
                if (roleLevel.get(list.get(i).getType()) >= 1) {
                    list.remove(i);
                }
            }
            return list;
        }
        else if (roleLevel.get(loggedInUser.getType()) == 2){
            List<User> list = userRepository.findAll();
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setPassword(null);
                if (roleLevel.get(list.get(i).getType()) >= 2) {
                    list.remove(i);
                }
            }
            return list;
        }
        else if (roleLevel.get(loggedInUser.getType()) == 3) {
            List<User> list = userRepository.findAll();
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setPassword(null);
            }
            return list;
        }
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");
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
        roleLevel.put("merchant", 0);
        roleLevel.put("consumer",0);
        User oneUser = userRepository.findById(userId);

        if (roleLevel.get(loggedInUser.getType()) == 0) {
            if (loggedInUser.getId().equals(userId)) {
                oneUser.setPassword(null);
                return oneUser;
            }
            else
                throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");
        }
        else if (roleLevel.get(loggedInUser.getType()) == 1 && roleLevel.get(oneUser.getType()) == 0 && loggedInUser.getAuthorization().equals("true")) {
            oneUser.setPassword(null);
            return oneUser;
        }
        else if (roleLevel.get(loggedInUser.getType()) == 2 && roleLevel.get(oneUser.getType()) < 2) {
            oneUser.setPassword(null);
            return oneUser;
        }
        else if (roleLevel.get(loggedInUser.getType()) == 3 && roleLevel.get(oneUser.getType()) < 3) {
            oneUser.setPassword(null);
            return oneUser;
        }
        else if (loggedInUser.getId().equals(oneUser.getId())) {
            oneUser.setPassword(null);
            return oneUser;
        }
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");

    }

    public void user_email_val(String userName, String email)
    {
        if (userRepository.findByUsername(userName) != null)
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "An user with same username already exits. Please use a different username.");
        if (userRepository.findByEmail(email) != null)
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "An user with same email already exits. Please use a different email.");
    }

    /*
         * Password should be less than 15 and more than 8 characters in length.
         * Password should contain at least one upper case and one lower case alphabet.
         * Password should contain at least one number.
         * Password should contain at least one special character.
         */

    public void passwordValidation(String userName, String password)
    {
        if (password.length() > 15 || password.length() < 8)
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Password should be less than 15 and more than 8 characters in length.");
        if (password.indexOf(userName) > -1)
        {
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Password Should not be same as user name");
        }
        String upperCaseChars = "(.*[A-Z].*)";
        if (!password.matches(upperCaseChars ))
        {
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Password should contain atleast one upper case alphabet");
        }
        String lowerCaseChars = "(.*[a-z].*)";
        if (!password.matches(lowerCaseChars ))
        {
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Password should contain atleast one lower case alphabet");
        }
        String numbers = "(.*[0-9].*)";
        if (!password.matches(numbers ))
        {
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Password should contain atleast one number.");
        }
        String specialChars = "(.*[,~,!,@,#,$,%,^,&,*,(,),-,_,=,+,[,{,],},|,;,:,<,>,/,?].*$)";
        if (!password.matches(specialChars ))
        {
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Password should contain atleast one special character");
        }
    }

    @POST
    public User createUser(User user, @HeaderParam("Authorization") String authorization){
        String[] types = {"tier1", "tier2", "administrator", "consumer","merchant"};
        String userType = user.getType();
        User loggedInUser = loggedInService.getLoggedInUser(authorization);

        user_email_val(user.getUsername(), user.getEmail());
        passwordValidation(user.getUsername(), user.getPassword());

        if(Arrays.asList(types).contains(userType)) {
            if (user.getType().equals("tier1") || user.getType().equals("tier2")) {
                if (loggedInUser.getType().equals("administrator")) {
                    if (user.getType().equals("tier1"))
                        user.setAuthorization("false");
                    user.setId(null);// ensure that id is set by database
                    return userRepository.save(user);
                }
                else
                    throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid auth");
            }
            else if (user.getType().equals("consumer") || user.getType().equals("merchant")) {
                if ((loggedInUser.getType().equals("tier1") && loggedInUser.getAuthorization().equals("true")) || loggedInUser.getType().equals("tier2") || loggedInUser.getType().equals("administrator")) {
                    user.setId(null);// ensure that id is set by database
                    return userRepository.save(user);
                }
                else
                    throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid auth");
            }
            else
                throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid auth");
        }
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid auth");
    }

    @PUT
    @Path("/requests/{userId}")
    public User createRequest(@PathParam("userId") String userId, @HeaderParam("Authorization") String authorization, User user) {
        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        User byId = userRepository.findById(userId);
        if (loggedInUser.getType().equals("tier1") && loggedInUser.getId().equals(userId) && user.getAuthorization().equals("pending") && loggedInUser.getAuthorization().equals("false")) {
            loggedInUser.setAuthorization(user.getAuthorization());
            return userRepository.save(loggedInUser);
        }
        else if (loggedInUser.getType().equals("tier2") && byId.getType().equals("tier1")  && (user.getAuthorization().equals("true") || user.getAuthorization().equals("false"))) {
            byId.setAuthorization(user.getAuthorization());
            return userRepository.save(byId);
        }
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid auth");
    }

    @GET
    @Path("/requests")
    public List<User> getRequests(@HeaderParam("Authorization") String authorization) {
        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        List<User> users = userRepository.findByType("tier1");
        if (loggedInUser.getType().equals("tier2")) {
            for (User user:users) {
                user.setPassword(null);
            }
            return users;
        }
        else if (loggedInUser.getType().equals("tier1")) {
            List<User> temp = new ArrayList<>();
            loggedInUser.setPassword(null);
            temp.add(loggedInUser);
            return temp;
        }
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid auth");

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
        roleLevel.put("merchant", 0);
        roleLevel.put("consumer", 0);

        if(loggedInUser.getId().equals(userId)) {
            loggedInUser.setName(user.getName());
            loggedInUser.setAddress(user.getAddress());
            loggedInUser.setPhoneNumber(user.getPhoneNumber());
            return userRepository.save(loggedInUser);
        }
        else if (roleLevel.get(loggedInUser.getType()) == 3 && (roleLevel.get(byId.getType()) == 2 || roleLevel.get(byId.getType()) == 1)) {
            byId.setName(user.getName());
            byId.setAddress(user.getAddress());
            byId.setPhoneNumber(user.getPhoneNumber());
            return userRepository.save(byId);
        }
        else if (roleLevel.get(loggedInUser.getType()) == 2 && roleLevel.get(byId.getType()) == 0) {
            byId.setName(user.getName());
            byId.setAddress(user.getAddress());
            byId.setPhoneNumber(user.getPhoneNumber());
            return userRepository.save(byId);
        }
        else if (roleLevel.get(loggedInUser.getType()) == 1 && roleLevel.get(byId.getType()) == 0 && loggedInUser.getAuthorization().equals("true")) {
            byId.setName(user.getName());
            byId.setAddress(user.getAddress());
            byId.setPhoneNumber(user.getPhoneNumber());
            return userRepository.save(byId);
        }
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid auth");
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
        roleLevel.put("merchant", 0);
        roleLevel.put("consumer", 0);
        if( roleLevel.get(loggedInUser.getType()) == 0)
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");
        else if (roleLevel.get(loggedInUser.getType()) == 2 && roleLevel.get(byId.getType()) == 0) {
            userRepository.deleteById(userId);
            return "{\"status\":\"success\"}";
        }
        else if (roleLevel.get(loggedInUser.getType()) == 3 && (roleLevel.get(byId.getType()) == 1 || roleLevel.get(byId.getType()) == 2)){
            userRepository.deleteById(userId);
            return "{\"status\":\"success\"}";
        }
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");
    }
}
