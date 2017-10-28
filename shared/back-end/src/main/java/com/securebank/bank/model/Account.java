package com.securebank.bank.model;

import org.springframework.data.annotation.Id;

public class Account {

    @Id
    private String id;

    private String userId;
    private Double amount;
    private String accountType; // "checking"/"savings"/"credit"

    public Account(String id, String userId, Double amount, String accountType) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.accountType = accountType;
    }

    public Account() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        //  Rounding off 12.5555555555555 > 12.55
        double roundOff = (double) Math.round(amount * 100) / 100;
        // this.amount = amount;
        this.amount = roundOff;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
