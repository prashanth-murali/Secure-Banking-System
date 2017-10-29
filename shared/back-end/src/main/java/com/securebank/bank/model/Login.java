package com.securebank.bank.model;

import com.securebank.bank.services.errors.ApplicationValidationError;

import javax.ws.rs.core.Response;
import java.security.MessageDigest;

public class Login {
    private String username;
    private String password;

    public Login() {
    }

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {

        String temp = null;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            temp = md.digest(password.getBytes()).toString();
        } catch (Exception e) {
            throw new ApplicationValidationError(Response.Status.UNAUTHORIZED, "hashing error 3");
        }

        return temp;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
