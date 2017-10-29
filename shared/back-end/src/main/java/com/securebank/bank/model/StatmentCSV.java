package com.securebank.bank.model;

public class StatmentCSV {
    StringBuilder sb = null;

    public StatmentCSV() {
        sb = new StringBuilder();
        setCSVHeaders();
    }

    private void setCSVHeaders() {
        sb.append("For Account");
        sb.append(',');
        sb.append("Transaction Date");
        sb.append(',');
        sb.append("type");
        sb.append(',');
        sb.append("amount");
        sb.append(',');
        sb.append("status");
        sb.append(',');
        sb.append("isCritical");
        sb.append('\n'); // new row
    }

    public void addCSVRow(String forAccount, Transaction transaction) {
        sb.append(String.valueOf(forAccount));
        sb.append(',');
        sb.append(String.valueOf(transaction.getCreatedDate()));
        sb.append(',');
        sb.append(String.valueOf(transaction.getType()));
        sb.append(',');
        sb.append(String.valueOf(transaction.getAmount()));
        sb.append(',');
        sb.append(String.valueOf(transaction.getStatus()));
        sb.append(',');
        sb.append(String.valueOf(transaction.getCritical()));
        sb.append('\n'); // new row
    }

    public String writeCSV(){
        return sb.toString();
    }
}
