package com.securebank.bank.services;

import com.securebank.bank.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {

    public User findByUsername(String firstName);
    public User findById(String userId);
    public void deleteById(String userId);
    public List<User> findAll();
}
