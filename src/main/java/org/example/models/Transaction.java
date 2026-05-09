package org.example.models;
import java.time.LocalDateTime;

public class Transaction {
    private int transId;
    private int walletId;
    private String type;
    private double amount;
    private String otherParty;
    private LocalDateTime timestamp;
    private String status;

    public Transaction(int transId, int walletId, String type,
                       double amount, String otherParty,
                       LocalDateTime timestamp, String status) {
        this.transId = transId;
        this.walletId = walletId;
        this.type = type;
        this.amount = amount;
        this.otherParty = otherParty;
        this.timestamp = timestamp;
        this.status = status;
    }

    // Getters
    public int getTransId()          { return transId; }
    public int getWalletId()         { return walletId; }
    public String getType()          { return type; }
    public double getAmount()        { return amount; }
    public String getOtherParty()    { return otherParty; }
    public LocalDateTime getTimestamp(){ return timestamp; }
    public String getStatus()        { return status; }
}
