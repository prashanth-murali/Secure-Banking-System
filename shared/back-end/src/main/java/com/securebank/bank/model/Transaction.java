 package com.securebank.bank.model;

import org.springframework.data.annotation.Id;

import java.util.Date;

 public class Transaction {

	@Id
    private String transactionId;
    private String fromAccountId;
    private String toAccountId;
    private String createdDate;
    private String type; //internal //external // payment
    private Double amount;
    private String status; // "pending", "approved", "denied"
    private Boolean isCritical;
    private String email;
    private String creditCard;
    private String cvv;

    public Transaction() {}

     public Transaction(String transactionId, String fromAccountId, String toAccountId, String createdDate, String type, Double amount, String status, Boolean isCritical, String email, String creditCard, String cvv) {
         this.transactionId = transactionId;
         this.fromAccountId = fromAccountId;
         this.toAccountId = toAccountId;
         this.createdDate = createdDate;
         this.type = type;
         this.amount = amount;
         this.status = status;
         this.isCritical = isCritical;
         this.email = email;
         this.creditCard = creditCard;
         this.cvv = cvv;
     }



     public String getTransactionId() {
         return transactionId;
     }

     public void setTransactionId(String transactionId) {
         this.transactionId = transactionId;
     }

     public String getFromAccountId() {
         return fromAccountId;
     }

     public void setFromAccountId(String fromAccountId) {
         this.fromAccountId = fromAccountId;
     }

     public String getToAccountId() {
         return toAccountId;
     }

     public void setToAccountId(String toAccountId) {
         this.toAccountId = toAccountId;
     }

     public String getCreatedDate() {
         return createdDate;
     }

     public void setCreatedDate(String createdDate) {
         this.createdDate = createdDate;
     }

     public String getType() {
         return type;
     }

     public void setType(String type) {
         this.type = type;
     }

     public Double getAmount() {
         return amount;
     }

     public void setAmount(Double amount) {
        //  Rounding off 12.5555555555555 > 12.55
        double roundOff = (double) Math.round(amount * 100) / 100;
        this.amount = roundOff;
     }

     public String getStatus() {
         return status;
     }

     public void setStatus(String status) {
         this.status = status;
     }

     public Boolean getCritical() {
         return isCritical;
     }

     public void setCritical(Boolean critical) {
         isCritical = critical;
     }

     public String getCreditCard() {
         return creditCard;
     }

     public void setCreditCard(String creditCard) {
         this.creditCard = creditCard;
     }

     public String getCvv() {
         return cvv;
     }

     public void setCvv(String cvv) {
         this.cvv = cvv;
     }

     public String getEmail() {
         return email;
     }

     public void setEmail(String email) {
         this.email = email;
     }

 }
