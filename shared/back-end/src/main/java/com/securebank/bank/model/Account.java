package com.securebank.bank.model;

import org.springframework.data.annotation.Id;

public class Account {

    @Id
    private String id;

    private String userId;
    private Double amount;
    private String accountType; // "checking"/"savings"/"credit"
    private String cardNumber;

    public Account(String id, String userId, Double amount, String accountType, String cardNumber) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.accountType = accountType;
        this.cardNumber = cardNumber;
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

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
