package group_5.banking_system_application.Ui;

import java.util.Date;

public class Transactions {
    private String senderID;
    private String description;
    private double amount;
    private String status;
    private Date date;
    private String beneficiary;
    private double balance;

    public Transactions(String description, double amount, String status, Date date, String beneficiary, double balance) {
        this.description = description;
        this.amount = amount;
        this.status = status;
        this.date = date;
        this.balance = balance;
    }

    public Transactions(){}

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(String beneficiary) {
        this.beneficiary = beneficiary;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
