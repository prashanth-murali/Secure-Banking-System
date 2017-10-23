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
        validationService.validateLoggedInUserIsAdmin(loggedInUser);
        // ensure that the user has permission to create an account for this user (the user themselves or tier1 or tier2)
        Map<String, Integer> roleLevel = new HashMap<String, Integer>();
        roleLevel.put("administrator", 3);
        roleLevel.put("tier2", 2);
        roleLevel.put("tier1", 1);
        roleLevel.put("external", 0);
        if( roleLevel.get(loggedInUser.getType()) < 1) {
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");
        }
        return accountRepository.findAll();
    }

    @GET
    @Path("/{accountId}")
    public Account getAccount(@PathParam("accountId") String accountId){
        return accountRepository.findById(accountId);
    }

    @GET
    @Path("/user/{userId}")
    public List<Account> getAccountsForUser(@PathParam("userId") String userId){
        return accountRepository.findByUserId(userId);
    }

    @POST
    public Account createAccount(Account account){
        //validate that the user_id matches a valid user valid

        // ensure that the user has permission to create an account for this user (the user themselves or tier1 or tier2)

        // validate account type is set to "checking", "savings" or "credit"

        account.setId(null);// ensure that id is set by database
        return accountRepository.save(account);
    }

    @PUT
    @Path("/{accountId}")
    public Account updateAccount(@PathParam("accountId") String accountId, Account account){
        Account byId = accountRepository.findById(accountId);

        byId.setAmount(account.getAmount()); // only allow the amount of an account to be updated

        return accountRepository.save(byId);
    }

    @DELETE
    @Path("/{accountId}")
    public String deleteAccount(@PathParam("accountId") String accountId){
        accountRepository.deleteById(accountId);
        return "{\"status\":\"success\"}";
    }
}
