package com.securebank.bank.resources;

import com.securebank.bank.model.Account;
import com.securebank.bank.model.User;
import com.securebank.bank.services.AccountRepository;
import com.securebank.bank.services.LoggedInService;
import com.securebank.bank.services.UserRepository;
import com.securebank.bank.services.ValidationService;
import org.apache.log4j.Logger;
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
import java.util.List;
import java.util.*;
import javax.ws.rs.core.Response;
import com.securebank.bank.services.errors.ApplicationValidationError;

@Component
@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource {
    Logger logger = Logger.getLogger(UserRepository.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    LoggedInService loggedInService;

    @Autowired
    ValidationService validationService;


    @GET
    public List<Account> getAccounts(@HeaderParam("Authorization") String authorization){
        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        // ensure that the user has permission to create an account for this user (the user themselves or tier1 or tier2)
        Map<String, Integer> roleLevel = new HashMap<String, Integer>();
        roleLevel.put("administrator", 3);
        roleLevel.put("tier2", 2);
        roleLevel.put("tier1", 1);
        roleLevel.put("external", 0);

        if (roleLevel.get(loggedInUser.getType()) == 0) {
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");
        }
        else if (roleLevel.get(loggedInUser.getType()) < 3)
            return accountRepository.findAll();
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");

    }

    @GET
    @Path("/{accountId}")
    public Account getAccount(@PathParam("accountId") String accountId, @HeaderParam("Authorization") String authorization){
        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        // ensure that the user has permission to create an account for this user (the user themselves or tier1 or tier2)
        Map<String, Integer> roleLevel = new HashMap<String, Integer>();
        roleLevel.put("administrator", 3);
        roleLevel.put("tier2", 2);
        roleLevel.put("tier1", 1);
        roleLevel.put("external", 0);
        Account account = accountRepository.findById(accountId);

        if (loggedInUser.getId().equals(account.getUserId()) && roleLevel.get(loggedInUser.getType()) == 0) {
            return accountRepository.findById(accountId);
        }
        else if (roleLevel.get(loggedInUser.getType()) > 0 && roleLevel.get(loggedInUser.getType()) < 3) {
            return accountRepository.findById(accountId);
        }
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");
    }

    @GET
    @Path("/user/{userId}")
    public List<Account> getAccountsForUser(@PathParam("userId") String userId, @HeaderParam("Authorization") String authorization){
        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        if (loggedInUser.getId().equals(userId))
            return accountRepository.findByUserId(userId);
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");
    }

    @POST
    public Account createAccount(Account account, @HeaderParam("Authorization") String authorization){
        //validate that the user_id matches a valid user valid
        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        Map<String, Integer> roleLevel = new HashMap<String, Integer>();
        roleLevel.put("administrator", 3);
        roleLevel.put("tier2", 2);
        roleLevel.put("tier1", 1);
        roleLevel.put("external", 0);
        // ensure that the user has permission to create an account for this user (the user themselves or tier1 or tier2)
        if (loggedInUser.getId().equals(account.getUserId()) && roleLevel.get(loggedInUser.getType()) == 0) {
            String acct = account.getAccountType();
            String[] types = {"checking", "savings", "credit"};
            if(Arrays.asList(types).contains(acct)) {
                account.setId(null);// ensure that id is set by database
                return accountRepository.save(account);
            }
            else
                throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid Account Type");
        }
        else if (roleLevel.get(loggedInUser.getType()) > 0 && roleLevel.get(loggedInUser.getType()) < 3) {
            String acct = account.getAccountType();
            String[] types = {"checking", "savings", "credit"};
            if(Arrays.asList(types).contains(acct)) {
                account.setId(null);// ensure that id is set by database
                return accountRepository.save(account);
            }
            else
                throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid Account Type");
        }
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");
    }

    @PUT
    @Path("/{accountId}")
    public Account updateAccount(@PathParam("accountId") String accountId, Account account, @HeaderParam("Authorization") String authorization){
        Account byId = accountRepository.findById(accountId);
        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        Map<String, Integer> roleLevel = new HashMap<String, Integer>();
        roleLevel.put("administrator", 3);
        roleLevel.put("tier2", 2);
        roleLevel.put("tier1", 1);
        roleLevel.put("external", 0);
        if (roleLevel.get(loggedInUser.getType()) > 0 && roleLevel.get(loggedInUser.getType()) < 3) {
            byId.setAmount(account.getAmount()); // only allow the amount of an account to be updated
            return accountRepository.save(byId);
        }
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");
    }

    @DELETE
    @Path("/{accountId}")
    public String deleteAccount(@PathParam("accountId") String accountId, @HeaderParam("Authorization") String authorization) {
        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        Map<String, Integer> roleLevel = new HashMap<String, Integer>();
        roleLevel.put("administrator", 3);
        roleLevel.put("tier2", 2);
        roleLevel.put("tier1", 1);
        roleLevel.put("external", 0);
        if (roleLevel.get(loggedInUser.getType()) == 2) {
            accountRepository.deleteById(accountId);
            return "{\"status\":\"success\"}";
        }
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");
    }
}
