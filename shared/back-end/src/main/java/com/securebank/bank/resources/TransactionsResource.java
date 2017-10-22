package com.securebank.bank.resources;

import com.securebank.bank.model.Account;
import com.securebank.bank.model.User;
import com.securebank.bank.model.Transaction;
import com.securebank.bank.services.UserRepository;
import com.securebank.bank.services.TransactionsRepository;
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

    // create trasaction
    // update transaction

    @POST
    public Transaction createTransaction(Transaction trans){
        trans.setTransactionId(null);// ensure the user does not pass their own id to mongo
        return transactionsRepository.save(trans);
    }

    @PUT
    @Path("/{transactionId}")
    public Transaction updateTransaction(@PathParam("transactionId") String transactionId, Transaction trans){
        Transaction byId = transactionsRepository.findByTransactionId(transactionId);
        BeanUtils.copyProperties(trans, byId);
        return transactionsRepository.save(byId);
    }



}
