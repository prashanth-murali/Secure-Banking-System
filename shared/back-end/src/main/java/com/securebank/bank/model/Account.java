package com.securebank.bank.model;

public class Account {
    private String id;
    private Double amount;
    private String accountType; // "checking"/"savings"/"cred"

    public Account(String id, Double amount, String accountType) {
        this.id = id;
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
        this.amount = amount;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

}
