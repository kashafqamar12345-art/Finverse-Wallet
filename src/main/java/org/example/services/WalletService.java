package org.example.services;

import org.example.dao.TransactionDAO;
import org.example.dao.WalletDAO;
import org.example.dao.UserDAO;
import org.example.models.Transaction;
import org.example.models.Wallet;

import java.util.Collections;
import java.util.List;
public class WalletService {

    private final WalletDAO      walletDAO      = new WalletDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final UserDAO        userDAO        = new UserDAO();

    private static final double MIN_BALANCE  = 100.0;
    private static final double MAX_DEPOSIT  = 100_000.0;
    private static final double MIN_TRANSFER = 10.0;


    // WALLET MANAGEMENT
    /** Get wallet by userId. */
    public Wallet getWallet(int userId) {
        return walletDAO.getWalletByUserId(userId);
    }

    /** Create a new wallet for the given userId. */
    public boolean createWallet(int userId) {
        return walletDAO.createWallet(userId);
    }


    // GET BALANCE  (param = userId)
    public double getBalance(int userId) {
        Wallet wallet = walletDAO.getWalletByUserId(userId);   // FIX: was getUserIdByWalletId
        return (wallet != null) ? wallet.getBalance() : 0.0;
    }


    // DEPOSIT  (param = userId)
    public String deposit(int userId, double amount) {

        if (amount <= 0) {
            return "ERROR: Deposit amount must be greater than zero.";
        }
        if (amount > MAX_DEPOSIT) {
            return "ERROR: Single deposit cannot exceed PKR " +
                    String.format("%,.0f", MAX_DEPOSIT) + ".";
        }

        // FIX: fetch wallet by userId directly
        Wallet wallet = walletDAO.getWalletByUserId(userId);
        if (wallet == null) {
            return "ERROR: Wallet not found for your account.";
        }

        double newBalance = wallet.getBalance() + amount;
        boolean updated   = walletDAO.updateBalance(wallet.getWalletId(), newBalance);

        if (updated) {
            transactionDAO.logTransaction(
                    wallet.getWalletId(), "DEPOSIT", amount,
                    "Self Deposit", "SUCCESS");
            return "SUCCESS: PKR " + String.format("%,.2f", amount) +
                    " deposited. New balance: PKR " + String.format("%,.2f", newBalance);
        }

        return "ERROR: Deposit failed. Please try again.";
    }


    // WITHDRAW  (param = userId)
    public String withdraw(int userId, double amount) {

        if (amount <= 0) {
            return "ERROR: Withdrawal amount must be greater than zero.";
        }

        // FIX: fetch wallet by userId directly
        Wallet wallet = walletDAO.getWalletByUserId(userId);
        if (wallet == null) {
            return "ERROR: Wallet not found for your account.";
        }

        if (wallet.getBalance() < amount) {
            return "ERROR: Insufficient balance. " +
                    "Your balance is PKR " + String.format("%,.2f", wallet.getBalance()) + ".";
        }

        if (wallet.getBalance() - amount < MIN_BALANCE) {
            return "ERROR: Must maintain a minimum balance of PKR " +
                    String.format("%,.2f", MIN_BALANCE) + ".";
        }

        double newBalance = wallet.getBalance() - amount;
        boolean updated   = walletDAO.updateBalance(wallet.getWalletId(), newBalance);

        if (updated) {
            transactionDAO.logTransaction(
                    wallet.getWalletId(), "WITHDRAW", amount,
                    "Self Withdrawal", "SUCCESS");
            return "SUCCESS: PKR " + String.format("%,.2f", amount) +
                    " withdrawn. Remaining balance: PKR " + String.format("%,.2f", newBalance);
        }

        transactionDAO.logTransaction(
                wallet.getWalletId(), "WITHDRAW", amount,
                "Self Withdrawal", "FAILED");
        return "ERROR: Withdrawal failed. Please try again.";
    }


    // TRANSFER  (senderUserId → receiverUsername)
    public String transfer(int senderUserId, String receiverUsername, double amount) {

        if (receiverUsername == null || receiverUsername.trim().isEmpty()) {
            return "ERROR: Please enter a recipient username.";
        }
        if (amount < MIN_TRANSFER) {
            return "ERROR: Minimum transfer amount is PKR " +
                    String.format("%,.2f", MIN_TRANSFER) + ".";
        }
        if (amount <= 0) {
            return "ERROR: Amount must be greater than zero.";
        }

        // Sender wallet
        Wallet sender = walletDAO.getWalletByUserId(senderUserId);
        if (sender == null) {
            return "ERROR: Your wallet was not found.";
        }

        // Self-transfer check
        var senderUser = userDAO.getUserById(senderUserId);
        if (senderUser != null &&
                senderUser.getUsername().equalsIgnoreCase(receiverUsername.trim())) {
            return "ERROR: You cannot transfer money to yourself.";
        }

        // Receiver wallet
        int receiverWalletId = walletDAO.getWalletIdByUsername(receiverUsername.trim());
        if (receiverWalletId == -1) {
            return "ERROR: User '" + receiverUsername + "' was not found.";
        }

        // Balance checks
        if (sender.getBalance() < amount) {
            return "ERROR: Insufficient balance. " +
                    "Current: PKR " + String.format("%,.2f", sender.getBalance()) + ".";
        }
        if (sender.getBalance() - amount < MIN_BALANCE) {
            return "ERROR: Must maintain a minimum balance of PKR " +
                    String.format("%,.2f", MIN_BALANCE) + " after transfer.";
        }

        // Atomic transfer
        boolean ok = walletDAO.transfer(sender.getWalletId(), receiverWalletId, amount);

        if (ok) {
            String senderName = (senderUser != null) ? senderUser.getUsername() : "Unknown";

            transactionDAO.logTransaction(
                    sender.getWalletId(), "TRANSFER", amount,
                    "To: " + receiverUsername.trim(), "SUCCESS");
            transactionDAO.logTransaction(
                    receiverWalletId, "DEPOSIT", amount,
                    "From: " + senderName, "SUCCESS");

            return "SUCCESS: PKR " + String.format("%,.2f", amount) +
                    " sent to " + receiverUsername + " successfully!";
        }

        transactionDAO.logTransaction(
                sender.getWalletId(), "TRANSFER", amount,
                "To: " + receiverUsername.trim(), "FAILED");
        return "ERROR: Transfer failed. No money was moved.";
    }

    // HISTORY & STATS  (param = userId)
    public List<Transaction> getHistory(int userId) {
        Wallet w = walletDAO.getWalletByUserId(userId);
        if (w == null) return Collections.emptyList();
        return transactionDAO.getTransactionHistory(w.getWalletId());
    }

    public double getTotalReceived(int userId) {
        Wallet w = walletDAO.getWalletByUserId(userId);
        if (w == null) return 0;
        return transactionDAO.getTotalReceived(w.getWalletId());
    }

    public double getTotalSpent(int userId) {
        Wallet w = walletDAO.getWalletByUserId(userId);
        if (w == null) return 0;
        return transactionDAO.getTotalSpent(w.getWalletId());
    }

    public int getTransactionCount(int userId) {
        Wallet w = walletDAO.getWalletByUserId(userId);
        if (w == null) return 0;
        return transactionDAO.getTransactionCount(w.getWalletId());
    }

    public String getSpendingSummary(int userId) {
        return "Current Balance : PKR " + String.format("%,.2f", getBalance(userId))  + "\n" +
                "Total Received  : PKR " + String.format("%,.2f", getTotalReceived(userId)) + "\n" +
                "Total Spent     : PKR " + String.format("%,.2f", getTotalSpent(userId));
    }
}