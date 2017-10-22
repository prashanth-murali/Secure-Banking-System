package com.securebank.bank.resources;

import com.securebank.bank.model.Transaction;
import com.securebank.bank.services.TransactionsRepository;
import com.securebank.bank.services.AccountRepository;
import com.securebank.bank.services.LoggedInService;
import org.springframework.beans.BeanUtils;
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

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    UserRepository userRepository;
    
    @GET// need to give some validation to only for administrator or external only can see their own transaction history
    public List<Transaction> getTransactions() {
        return transactionsRepository.findAll();
    }

    @GET
    @Path("/{transactionId}")
    public Transaction getTransaction(@PathParam("transactionId") String transId){
        // give me Llist<transactions> by accountId = to or form
        return transactionsRepository.findByTransactionId(transId);
    }

    // create trasaction // also need do the transaction not only by account id but also by email or phone in User model
    @POST
    public Transaction createTransaction(Transaction trans){
        //all of this validation function could be refactored into XXservice
        Account target_account = accountRepository.findById(trans.toAccountId);
        Account my_account = accountRepository.findById(trans.fromAccountId);
        //make sure the target account is exist 
        //if (target_account == null) bad request

        //make sure my_account money is enough for trasaction
        //if (my_account.amount <= 0.0 || my_account.amount < trans.amount) bad request

        //define the critical transaction: if per transaction > 50000 -> critical transaciotn
        if (trans.amount > 5000) trans.setCritical(true);
        
        // Do create the transaction
        if (trans.getCritical()) {// if is critical, put it in pending and do updated later by administrator
            trans.setStatus("pending");
            trans.setCreatedDate(new Date());
            trans.setTransactionId(null);// ensure the user does not pass their own id to mongo
            return transactionsRepository.save(trans);
        } 
        else {
            trans.setStatus("approved");
            double my_remain = my_account.getAmount() - trans.amount;
            my_account.setAmount(my_remain);
            double target_remain = target_account.getAmount() + trans.amount;
            target_account.setAmount(target_remain);
            trans.setCreatedDate(new Date());
            trans.setTransactionId(null);// ensure the user does not pass their own id to mongo
            return transactionsRepository.save(trans);
        }
    }
    @GET
    @Path("/account/{accountId}")
    public List<Transaction> getAllTransactionsByAccount(@PathParam("accountId") String accountId){
        return transactionsRepository.findByFromAccountIdEqualsOrToAcccountIdEquals(accountId,accountId);
    }

    
    
    // update transaction
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
