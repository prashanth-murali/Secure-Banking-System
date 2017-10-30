package com.securebank.bank.resources;

import com.securebank.bank.services.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.securebank.bank.model.Account;
import com.securebank.bank.model.User;
import com.securebank.bank.services.LoggedInService;
import com.securebank.bank.services.UserRepository;
import com.securebank.bank.services.ValidationService;
import java.util.*;

@Component
@Path("/script")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class Script {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    UserRepository userRepository;

    @GET
    public void getInterest() {
        List<Account> accounts = accountRepository.findByAccountType("savings");
        for (Account account : accounts) {
            account.setAmount(account.getAmount() * 1.01);
            System.out.println(account.getAmount());
            accountRepository.save(account);
        }

        List<User> lists = userRepository.findAll();
        List<User> all = new ArrayList<>();
        for (User user : lists) {
            if (user.getType().equals("consumer") || user.getType().equals(("merchant")))
                all.add(user);
        }
        for (User user : all) {
            List<Account> allaccount = accountRepository.findByUserId(user.getId());
            double credit = 0;
            double noncredit = 0;
            for (Account account : allaccount) {
                if (account.getAccountType().equals("credit")) {
                    System.out.println(account);
                    credit = account.getCreditshopping();
                }
                else
                    noncredit += account.getAmount();
            }

            if (credit <= noncredit) {
                double tempCredit = credit;
                for (Account account : allaccount) {
                    if (account.getAccountType().equals("checking") || account.getAccountType().equals("savings")){
                        double minus = Math.min(account.getAmount(), tempCredit);
                        account.setAmount(account.getAmount() - minus);
                        tempCredit -= minus;
                        accountRepository.save(account);
                    }
                }

            }
            else {
                for (Account account : allaccount) {
                    if (account.getAccountType().equals("credit")) {
                        account.setCreditshopping(account.getCreditshopping() + 10);
                        accountRepository.save(account);
                    }
                }
            }
        }

    }



}

