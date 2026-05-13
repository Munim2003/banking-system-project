package group_5.banking_system_application.Ui;

import com.google.cloud.firestore.DocumentSnapshot;

import java.util.Date;

public class Transactions {
    /** "IN" = money received (green), "OUT" = sent (red). */
    public static final String FLOW_IN = "IN";
    public static final String FLOW_OUT = "OUT";

    private String senderID;
    private String description;
    private double amount;
    private String status;
    private Date date;
    private String beneficiary;
    private double balance;
    private String flow = FLOW_OUT;

    public Transactions(String description, double amount, String status, Date date, String beneficiary, double balance) {
        this.description = description;
        this.amount = amount;
        this.status = status;
        this.date = date;
        this.beneficiary = beneficiary;
        this.balance = balance;
    }

    public Transactions() {}

    public static Transactions fromFirestore(DocumentSnapshot doc) {
        Transactions t = new Transactions();
        t.setDescription(doc.getString("description"));
        t.setAmount(FirestoreMoney.toDouble(doc.get("amount")));
        t.setStatus(doc.getString("status") != null ? doc.getString("status") : "Completed");
        t.setDate(FirestoreMoney.toDate(doc, "createdAt"));
        String cp = doc.getString("counterpartyEmail");
        t.setBeneficiary(cp != null ? cp : "");
        String fl = doc.getString("flow");
        t.setFlow(fl != null ? fl : FLOW_OUT);
        t.setBalance(FirestoreMoney.toDouble(doc.get("balanceAfter")));
        return t;
    }

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

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }
}
