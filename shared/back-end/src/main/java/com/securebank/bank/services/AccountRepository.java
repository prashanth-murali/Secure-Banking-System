package com.securebank.bank.services;

import com.securebank.bank.model.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AccountRepository extends MongoRepository<Account, String> {

    public Account findById(String id);
    public List<Account> findByUserId(String userId);
    public void deleteById(String id);
    public List<Account> findAll();
    public List<Account> findByAccountType(String type);
    public Account findByCardNumber(String number);
}
