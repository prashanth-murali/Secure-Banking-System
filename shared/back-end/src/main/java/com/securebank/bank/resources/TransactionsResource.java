package com.securebank.bank.resources;

import com.securebank.bank.model.Account;
import com.securebank.bank.model.StatmentCSV;
import com.securebank.bank.model.Transaction;
import com.securebank.bank.model.User;
import com.securebank.bank.services.AccountRepository;
import com.securebank.bank.services.EmailService;
import com.securebank.bank.services.LoggedInService;
import com.securebank.bank.services.TransactionsRepository;
import com.securebank.bank.services.UserRepository;
import com.securebank.bank.services.errors.ApplicationValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionsResource {
    Logger logger = LoggerFactory.getLogger(TransactionsResource.class);

    @Autowired
    TransactionsRepository transactionsRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LoggedInService loggedInService;

    @Autowired
    EmailService emailService;

    @GET// need to give some validation to only for administrator or external only can see their own transaction history
    public List<Transaction> getTransactions(@HeaderParam("Authorization") String authorization) {
        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        Map<String, Integer> roleLevel = new HashMap<String, Integer>();
        roleLevel.put("administrator", 3);
        roleLevel.put("tier2", 2);
        roleLevel.put("tier1", 1);
        roleLevel.put("merchant", 0);
        roleLevel.put("consumer", 0);

        if (roleLevel.get(loggedInUser.getType()) == 0 || roleLevel.get(loggedInUser.getType()) == 3) {
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");
        }
        else if (roleLevel.get(loggedInUser.getType()) == 1 || roleLevel.get(loggedInUser.getType()) == 2)
            return transactionsRepository.findAll();
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");
    }

    @GET
    @Path("/{transactionId}")
    public Transaction getTransaction(@PathParam("transactionId") String transId, @HeaderParam("Authorization") String authorization){
        // give me Llist<transactions> by accountId = to or form
        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        // ensure that the user has permission to create an account for this user (the user themselves or tier1 or tier2)
        Map<String, Integer> roleLevel = new HashMap<String, Integer>();
        roleLevel.put("administrator", 3);
        roleLevel.put("tier2", 2);
        roleLevel.put("tier1", 1);
        roleLevel.put("merchant", 0);
        roleLevel.put("consumer", 0);

        Transaction transaction = transactionsRepository.findByTransactionId(transId);
        Account fromAccount = accountRepository.findById(transaction.getFromAccountId());
        Account toAccount = accountRepository.findById(transaction.getToAccountId());

        User fromUser = userRepository.findById(fromAccount.getUserId());
        User toUser = userRepository.findById(toAccount.getUserId());

        if ((loggedInUser.getId().equals(fromAccount.getUserId()) || loggedInUser.getId().equals(toAccount.getUserId())) && roleLevel.get(loggedInUser.getType()) == 0) {
            return transaction;
        }
        else if (roleLevel.get(loggedInUser.getType()) == 1 || roleLevel.get(loggedInUser.getType()) == 2) {
            return transaction;
        }
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Not Authorized");
    }

    // create trasaction // also need do the transaction not only by account id but also by email or phone in User model
    @POST
    public Transaction createTransaction(Transaction trans, @HeaderParam("Authorization") String authorization){
        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        Map<String, Integer> roleLevel = new HashMap<String, Integer>();
        roleLevel.put("administrator", 3);
        roleLevel.put("tier2", 2);
        roleLevel.put("tier1", 1);
        roleLevel.put("merchant", 0);
        roleLevel.put("consumer", 0);

        Account target_account = accountRepository.findById(trans.getToAccountId());
        Account my_account = accountRepository.findById(trans.getFromAccountId());
        User fromUser = userRepository.findById(my_account.getUserId());
        //User toUser = userRepository.findById(target_account.getUserId());

        if(target_account.getAccountType().equals("credit"))
        {
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED,"Cant send money to Credit Account.");
        }

        //if transaction times exceed 25
        List<Transaction> transactions = transactionsRepository.findByFromAccountId(trans.getFromAccountId());
        if (transactions.size() > 25)
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Transaction over 25 times per day");

        //sent by email
        if (trans.getType().equals("via_email") && my_account.getAccountType().equals("checking")) {
            if (roleLevel.get(loggedInUser.getType()) == 0) {
                User toUser = userRepository.findByEmail(trans.getEmail());
                if (toUser == null) {
                    throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "target user not exist");
                }
                else if (toUser.equals(loggedInUser)) {
                    throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Cannot sent to yourself by email");
                }
                else {
                    int checking = 0;
                    List<Account> account = accountRepository.findByUserId(toUser.getId());


                    for (int i = 0; i < account.size(); i++) {
                        if (account.get(i).getAccountType().equals("checking")){
                            target_account = account.get(i);
                            trans.setToAccountId(target_account.getId());
                            checking = 1;
                            logger.info("get account");
                        }
                    }
                    if (checking == 0)
                        throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "target checking account not exist");
                }
            }
        }


        //make sure the target account is exist
        if (target_account == null || my_account == null) {
            logger.info("Unable to find account from Authorization");
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid Auth");
        }

        //make sure my_account money is enough for trasaction
        if (my_account.getAmount() <= 0.0 || my_account.getAmount() < Math.abs(trans.getAmount())) {
            if(trans.getFromAccountId().equals(trans.getToAccountId()) && trans.getAmount() < 0 )
            {
                logger.info("Unable to transaction, money is not enough");
                throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid Auth");
            }

            if(!trans.getFromAccountId().equals(trans.getToAccountId()))
            {
                logger.info("Unable to transaction, money is not enough");
                throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid Auth");
            }

        }

        //define the critical transaction: all day transaction amount > 5000 -> critical transaciotn
        double critical_limit = 0.0;
        List<Transaction> list = transactionsRepository.findByFromAccountIdEqualsOrToAccountIdEquals(my_account.getId(), my_account.getId());
        if (list != null){
            for (Transaction transaction_history : list) {
                if (my_account.getId().equals(transaction_history.getFromAccountId())) {
                    critical_limit = critical_limit + Math.abs(transaction_history.getAmount());
                }
            }
        }

        critical_limit = critical_limit + Math.abs(trans.getAmount());

        if (critical_limit > Math.abs(5000)) trans.setCritical(true);
        else trans.setCritical(false);


        //Define who is going to create transaction, create the transaction method by account number, phone or email
        if (loggedInUser.getId().equals(my_account.getUserId()) && roleLevel.get(loggedInUser.getType()) == 0) {
            if (trans.getCritical()) {
                String emailMessageBody = "Dear Customer, your account has been requested a critical transaction. If there is any question, feel free to contact with us!!!";
                emailService.sendEmail(loggedInUser.getEmail(), emailMessageBody);
            }
            trans.setStatus("pending");
            trans.setCreatedDate(new Date().toString());
            trans.setTransactionId(null);// ensure the user does not pass their own id to mongo
            return transactionsRepository.save(trans);
        }
        else if (roleLevel.get(loggedInUser.getType()) == 1) {
            if (trans.getCritical()) {// if is critical, put it in pending and do updated later by administrator
                String emailMessageBody = "Dear Customer, your account has been requested a critical transaction by the tier1 staff. Waiting for the authorization by manager. If there is any question, feel free to contact with us!!!";
                emailService.sendEmail(fromUser.getEmail(), emailMessageBody);
                trans.setStatus("pending");
                trans.setCreatedDate(new Date().toString());
                trans.setTransactionId(null);// ensure the user does not pass their own id to mongo
                return transactionsRepository.save(trans);
            }
            else {
                trans.setStatus("approved");
                double my_remain = my_account.getAmount() - trans.getAmount();
                my_account.setAmount(my_remain);
                double target_remain = target_account.getAmount() + trans.getAmount();
                target_account.setAmount(target_remain);
                trans.setCreatedDate(new Date().toString());
                trans.setTransactionId(null);// ensure the user does not pass their own id to mongo
                accountRepository.save(my_account);
                accountRepository.save(target_account);
                return transactionsRepository.save(trans);
            }
        }
        else if (roleLevel.get(loggedInUser.getType()) == 2) {
            if (trans.getCritical()) {
                String emailMessageBody = "Dear Customer, your account has been requested a critical transaction by our manager. If there is any question, feel free to contact with us!!!";
                emailService.sendEmail(fromUser.getEmail(), emailMessageBody);
            }
            trans.setStatus("approved");
            double my_remain = my_account.getAmount() - trans.getAmount();
            my_account.setAmount(my_remain);
            double target_remain = target_account.getAmount() + trans.getAmount();
            target_account.setAmount(target_remain);
            trans.setCreatedDate(new Date().toString());
            trans.setTransactionId(null);// ensure the user does not pass their own id to mongo
            accountRepository.save(my_account);
            accountRepository.save(target_account);
            return transactionsRepository.save(trans);
        }
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid Auth");
    }

    @GET
    @Path("/account/{accountId}")
    public List<Transaction> getAllTransactionsByAccount(@PathParam("accountId") String accountId, @HeaderParam("Authorization") String authorization){
        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        Account account = accountRepository.findById(accountId);
        if (loggedInUser.getId().equals(account.getUserId()))
            return transactionsRepository.findByFromAccountIdEqualsOrToAccountIdEquals(accountId,accountId);
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid Auth");
    }


    @GET
    @Path("/payments")
    public List<Account> getAllMerchantAccounts (@HeaderParam("Authorization") String authorization){
        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        if (loggedInUser.getType().equals("consumer")) {
            List<Account> accounts = new ArrayList<>();
            List<User> users = userRepository.findByType("merchant");
            for (User user : users) {
                List<Account> temp = accountRepository.findByUserId(user.getId());
                for (Account account : temp) {
                    if (account.getAccountType().equals("checking")){
                        account.setName(user.getName());
                        accounts.add(account);

                    }
                }
            }
            return accounts;
        }
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid Auth");
    }

    @POST
    @Path("/payments")
    public Transaction makePayment (@HeaderParam("Authorization") String authorization, Transaction trans){
        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        Account fromacc = accountRepository.findByCardNumber(trans.getCreditCard());
        Account toacc = accountRepository.findById(trans.getToAccountId());

        System.out.println("credit card :" +trans.getCreditCard());
        System.out.println("to acc id :" +trans.getToAccountId());
        System.out.println("loggedInUser :" +loggedInUser.getUsername());

        if (loggedInUser.getType().equals("consumer") && fromacc.getAccountType().equals("credit") && toacc.getAccountType().equals("checking")) {
            if (fromacc.getCardNumber().equals(trans.getCreditCard()) && fromacc.getCvv().equals(trans.getCvv()) && fromacc.getAmount() >= trans.getAmount()) {
                if (trans.getAmount() > 0) {
                    trans.setStatus("pending");
                    trans.setCreatedDate(new Date().toString());
                    trans.setTransactionId(null);// ensure the user does not pass their own id to mongo
                    trans.setType(fromacc.getAccountType());
                    trans.setFromAccountId(fromacc.getId());
                    return transactionsRepository.save(trans);
                }
                else
                    throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Wrong Amount");
            }
            else
                throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Credit card validation wrong or credit is not enough");
        }
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid Auth");
    }

    @GET
    @Path("/payments/requests")
    public List<Transaction> getPaymentRequests (@HeaderParam("Authorization") String authorization){
        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        List<Account> accounts = accountRepository.findByUserId(loggedInUser.getId());
        Account merchantacc = new Account();
        for (Account account : accounts) {
            if (account.getAccountType().equals("checking"))
                merchantacc = account;
        }
        if (loggedInUser.getType().equals("merchant")) {
            List<Transaction> transactions = transactionsRepository.findByToAccountId(merchantacc.getId());
            return transactions;
        }
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid Auth");
    }

    @PUT
    @Path("/payments/requests/{transactionId}")
    public Transaction updatePayment (@HeaderParam("Authorization") String authorization, @PathParam("transactionId") String transactionId, Transaction trans){
        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        List<Account> accounts = accountRepository.findByUserId(loggedInUser.getId());
        Transaction byTransaction = transactionsRepository.findByTransactionId(transactionId);
        Account consumeracc  = accountRepository.findById(byTransaction.getFromAccountId());
        Account merchantacc = accountRepository.findById(byTransaction.getToAccountId());

        System.out.println(byTransaction.getFromAccountId());
        System.out.println(byTransaction.getFromAccountId());


        if (consumeracc == null || merchantacc == null)
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Account not exist");


        if (loggedInUser.getType().equals("merchant") && merchantacc.getId().equals(byTransaction.getToAccountId())) {
            if (trans.getStatus().equals("approved")) {
                if (byTransaction.getAmount() <= consumeracc.getAmount()) {
                    byTransaction.setStatus("approved");
                    double myRemain = consumeracc.getAmount() - byTransaction.getAmount();
                    consumeracc.setAmount(myRemain);
                    double targetRemain = merchantacc.getAmount() + byTransaction.getAmount();
                    merchantacc.setAmount(targetRemain);
                    accountRepository.save(consumeracc);
                    accountRepository.save(merchantacc);
                    return transactionsRepository.save(byTransaction);
                }
                else {
                    byTransaction.setStatus("denied");
                    return transactionsRepository.save(byTransaction);
                }

            }
            else if (trans.getStatus().equals("denied")) {
                byTransaction.setStatus("denied");
                return transactionsRepository.save(byTransaction);
            }
            else
                throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid Auth");
        }
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid Auth");
    }

    @GET
    @Path("/statement/{accountId}") // "An external user should be able to download banking statements."
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getStatment(@HeaderParam("Authorization") String authorization, @PathParam("accountId") String accountId) throws UnsupportedEncodingException {
        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        Account account = accountRepository.findById(accountId);

        if(account == null){
            throw new ApplicationValidationError(Response.Status.NOT_FOUND, "Account cannot be found");
        }

        if(!account.getUserId().equals(loggedInUser.getId())){
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid Auth");
        }


        if(loggedInUser.getType().equals("consumer") || loggedInUser.getType().equals("merchant")){
            StatmentCSV statmentCSV = new StatmentCSV();
            List<Transaction> transactions = transactionsRepository.findByFromAccountIdEqualsOrToAccountIdEquals(account.getId(), account.getId());
            for (Transaction transaction : transactions) {
                statmentCSV.addCSVRow(account.getId(),transaction);
            }

            String csvContent = statmentCSV.writeCSV();
            InputStream stream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8.name()));
            return Response.ok(stream,MediaType.APPLICATION_OCTET_STREAM).header("Content-Disposition", "attachment; filename=\"statement.csv\"").build();
        }else{
            logger.info("only external users can do this!");
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid Auth");
        }
    }


    // update transaction, approve for the critical transaction
    @PUT
    @Path("/{transactionId}")
    public Transaction updateTransaction(@PathParam("transactionId") String transactionId, Transaction trans, @HeaderParam("Authorization") String authorization){
        Transaction byId = transactionsRepository.findByTransactionId(transactionId);
        Account targetAccount = accountRepository.findById(byId.getToAccountId());
        Account myAccount = accountRepository.findById(byId.getFromAccountId());

        User loggedInUser = loggedInService.getLoggedInUser(authorization);
        Map<String, Integer> roleLevel = new HashMap<String, Integer>();
        roleLevel.put("administrator", 3);
        roleLevel.put("tier2", 2);
        roleLevel.put("tier1", 1);
        roleLevel.put("merchant", 0);
        roleLevel.put("consumer", 0);

        if (byId.getCritical() && byId.getStatus().equals("pending") && roleLevel.get(loggedInUser.getType()) == 2) {

            //make sure the target account is exist
            if (targetAccount == null || myAccount == null) {
                logger.info("Unable to find account from Authorization");
                byId.setStatus("denied");
                return transactionsRepository.save(byId);
            }

            //make sure my_account money is enough for trasaction
            if (myAccount.getAmount() <= 0.0 || myAccount.getAmount() < Math.abs(byId.getAmount())) {

                if(byId.getFromAccountId().equals(byId.getToAccountId()) && byId.getAmount()<0) {
                    logger.info("Unable to transaction, money is not enough");
                    byId.setStatus("denied");
                    return transactionsRepository.save(byId);
                }

                if(!byId.getFromAccountId().equals(byId.getToAccountId())) {
                    logger.info("Unable to transaction, money is not enough");
                    byId.setStatus("denied");
                    return transactionsRepository.save(byId);
                }
            }

            if (trans.getStatus().equals("approved")) {
                byId.setStatus("approved");
                double myRemain = myAccount.getAmount() - byId.getAmount();
                myAccount.setAmount(myRemain);
                double targetRemain = targetAccount.getAmount() + byId.getAmount();
                targetAccount.setAmount(targetRemain);
                byId.setCreatedDate(new Date().toString());
                accountRepository.save(myAccount);
                accountRepository.save(targetAccount);
                return transactionsRepository.save(byId);
            }
            else if (trans.getStatus().equals("denied")) {
                byId.setStatus("denied");
                return transactionsRepository.save(byId);
            }

        }
        else if (!byId.getCritical() && byId.getStatus().equals("pending") && (roleLevel.get(loggedInUser.getType()) == 1 || roleLevel.get(loggedInUser.getType()) == 2)) {
            //make sure the target account is exist
            if (targetAccount == null || myAccount == null) {
                logger.info("Unable to find account from Authorization");
                byId.setStatus("denied");
                return transactionsRepository.save(byId);
            }

            //make sure my_account money is enough for trasaction
            if (myAccount.getAmount() <= 0.0 || myAccount.getAmount() < Math.abs(byId.getAmount())) {
                if(byId.getFromAccountId().equals(byId.getToAccountId()) && byId.getAmount()<0)
                {
                logger.info("Unable to transaction, money is not enough");
                byId.setStatus("denied");
                return transactionsRepository.save(byId);
                }

                if(!byId.getFromAccountId().equals(byId.getToAccountId()))
                {
                    logger.info("Unable to transaction, money is not enough");
                    byId.setStatus("denied");
                    return transactionsRepository.save(byId);
                }
            }

            if (trans.getStatus().equals("approved")) {
                byId.setStatus("approved");
                double myRemain = myAccount.getAmount() - byId.getAmount();
                myAccount.setAmount(myRemain);
                double targetRemain = targetAccount.getAmount() + byId.getAmount();
                targetAccount.setAmount(targetRemain);
                byId.setCreatedDate(new Date().toString());
                accountRepository.save(myAccount);
                accountRepository.save(targetAccount);
                return transactionsRepository.save(byId);
            }
            else if (trans.getStatus().equals("denied")) {
                byId.setStatus("denied");
                return transactionsRepository.save(byId);
            }
        }
        else
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "Invalid Auth");

        return transactionsRepository.save(byId);
    }


}
