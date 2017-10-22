package com.securebank.bank.services;

import com.securebank.bank.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionsRepository extends MongoRepository<Transaction, String> {

    public Transaction findByTransactionId(String transactionId);
    public List<Transaction> findByFromAccountIdEqualsOrToAcccountIdEquals(String fromAccountId, String toAccountId);
    //public void deleteByTransactionId(String id);
    public List<Transaction> findAll();
}
