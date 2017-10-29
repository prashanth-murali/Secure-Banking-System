package com.securebank.bank.model;

public class Login {
    private String username;
    private String password;
    private String otptoken;

    public Login() {
    }

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
        this.otptoken = null;
    }

    public Login(String username, String password, String otptoken) {
        this.username = username;
        this.password = password;
        this.otptoken = otptoken;
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
        this.password = password;
    }

    public String getOtptoken() {
        return this.otptoken;
    }
}
