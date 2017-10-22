package com.securebank.bank.services;

import com.securebank.bank.model.User;
import com.securebank.bank.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionsRepository extends MongoRepository<Transaction, String> {

//    public Transaction findByUsername(String firstName);
    public Transaction findByTransactionId(String transactionId);
//    public void deleteById(String userId);
    public List<Transaction> findAll();
}
