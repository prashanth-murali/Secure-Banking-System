package com.securebank.bank.model;

import com.securebank.bank.services.errors.ApplicationValidationError;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.ws.rs.core.Response;
import java.security.*;

public class User {

    @Id
    private String id;
    private String type;// tier1,tier2,administrator, merchant, consumer
    private String name;
    private String address;
    private String phoneNumber;
    private String authorization;

    @Indexed(unique = true)
    private String username;
    private String password;
    private String email;


    public User() {}

    public User(String id, String type, String name, String address, String phoneNumber, String username, String password, String email, String authorization) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
        this.email = email;
        this.authorization = authorization;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            this.password = md.digest(password.getBytes()).toString();
        } catch (Exception e) {
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "hashing error");
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
