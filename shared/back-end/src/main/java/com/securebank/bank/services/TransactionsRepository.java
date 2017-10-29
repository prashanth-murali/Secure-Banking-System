package com.securebank.bank.services;

import com.securebank.bank.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionsRepository extends MongoRepository<Transaction, String> {

    public Transaction findByTransactionId(String transactionId);
    public List<Transaction> findByFromAccountIdEqualsOrToAccountIdEquals(String fromAccountId, String toAccountId);
    public List<Transaction> findAll();
    public List<Transaction> findByFromAccountId(String id);
}
