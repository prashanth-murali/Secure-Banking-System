package com.securebank.bank.resources;

import com.securebank.bank.model.Transaction;
import com.securebank.bank.services.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;
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
        // todo:
        // validate the fromAccountId is a real account
        // validate the fromAccountId belongs to the user
        // validate the toAcccountId is a real account
        // ect ect
        trans.setTransactionId(null);// ensure the user does not pass their own id to mongo
        trans.setCreatedDate(new Date());
        trans.setStatus("pending");// todo: if it is a critical transaction it is pending, otherwise effect is immeditate
        // todo: deduct money from accounts
        return transactionsRepository.save(trans);
    }

    @PUT
    @Path("/{transactionId}")
    public Transaction updateTransaction(@PathParam("transactionId") String transactionId, Transaction trans){
        // todo: the only things that can be updated is the status
        Transaction byId = transactionsRepository.findByTransactionId(transactionId);

        byId.setStatus(trans.getStatus());// only allow the status to be updated on transaction

        return transactionsRepository.save(byId);
    }

    // we don't want transactions to be deleted ever, they should be read only
//    @DELETE
//    @Path("/{transactionId}")
//    public String deleteTransaction(@PathParam("transactionId") String transactionId){
//        transactionsRepository.deleteByTransactionId(transactionId);
//        return "{\"status\":\"success\"}";
//    }


}
