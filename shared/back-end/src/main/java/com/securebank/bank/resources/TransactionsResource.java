package com.securebank.bank.resources;

import com.securebank.bank.model.Account;
import com.securebank.bank.model.User;
import com.securebank.bank.model.Transaction;
import com.securebank.bank.services.UserRepository;
import com.securebank.bank.services.TransactionsRepository;
//import the error handling

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionsResource {

    @Autowired
    TransactionsRepository transactionsRepository;

    @GET
    public List<Transaction> getTransactions() {
        return transactionsRepository.findAll();
    }

    @GET
    @Path("/{transactionId}")
    public Transaction getTransaction(@PathParam("transactionId") String transId){
        // give me Llist<transactions> by accountId = to or form
        return transactionsRepository.findByTransactionId(transId);
    }

    //Get all Critical Transaction

    // create trasaction
    @POST
    public Transaction createTransaction(Transaction trans){
        //all of this validation function could be refactored into XXservice
        //check the permission first to see who is going to createTransaction
        //()
        //check the account have enough money or not
        if (accountResource.getRemain(trans.fromAccountId) > trans.amount && trans.amount > 0.0 && trans.isCritical) {
            //Trans create
            accountResource.
            trans.setTransactionId(null);// ensure the user does not pass their own id to mongo
            return transactionsRepository.save(trans);
        }
        //get the trans.fromAccountId
        //get the trans.toAccountId

        // subtract from the from account, add to the to account
        // update account value




        trans.setTransactionId(null);// ensure the user does not pass their own id to mongo
        return transactionsRepository.save(trans);
    }
    
    // update transaction
    @PUT
    @Path("/{transactionId}")
    public Transaction updateTransaction(@PathParam("transactionId") String transactionId, Transaction trans){
        Transaction byId = transactionsRepository.findByTransactionId(transactionId);
        BeanUtils.copyProperties(trans, byId);
        return transactionsRepository.save(byId);
    }



}
