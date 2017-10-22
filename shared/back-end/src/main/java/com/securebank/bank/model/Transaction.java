 package com.securebank.bank.model;

import org.springframework.data.annotation.Id;

import java.util.Date;

 public class Transaction {

	@Id
    private String transactionId;
    private String fromAccountId;
    private String toAcccountId;
    private Date createdDate;
    private String type;
    private Double amount;
    private String status; // "pending", "approved", "denied"

    public Transaction() {}

     public Transaction(String transactionId, String fromAccountId, String toAcccountId, Date createdDate, String type, Double amount, String status) {
         this.transactionId = transactionId;
         this.fromAccountId = fromAccountId;
         this.toAcccountId = toAcccountId;
         this.createdDate = createdDate;
         this.type = type;
         this.amount = amount;
         this.status = status;
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

     public String getToAcccountId() {
         return toAcccountId;
     }

     public void setToAcccountId(String toAcccountId) {
         this.toAcccountId = toAcccountId;
     }

     public Date getCreatedDate() {
         return createdDate;
     }

     public void setCreatedDate(Date createdDate) {
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
         this.amount = amount;
     }

     public String getStatus() {
         return status;
     }

     public void setStatus(String status) {
         this.status = status;
     }
 }