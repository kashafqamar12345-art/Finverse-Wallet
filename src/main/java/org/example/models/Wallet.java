package org.example.models;

public class Wallet {
    private int walletId;
    private int userId;
    private double balance;
    private String currency;

    public Wallet(int walletId, int userId,
                  double balance, String currency) {
        this.walletId = walletId;
        this.userId = userId;
        this.balance = balance;
        this.currency = currency;
    }

    public int getWalletId()    { return walletId; }
    public int getUserId()      { return userId; }
    public double getBalance()  { return balance; }
    public String getCurrency() { return currency; }
    public void setBalance(double balance) {
        this.balance = balance;
    }
}
