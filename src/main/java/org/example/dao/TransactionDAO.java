package org.example.dao;

import org.example.db.DBConnection;
import org.example.models.Transaction;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    // Save a transaction record to the database
    public boolean logTransaction(int walletId,
                                  String type,
                                  double amount,
                                  String otherParty,
                                  String status) {
        String sql = "INSERT INTO Transaction " +
                "(Wallet_ID, Type, Amount, Other_Party, Status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, walletId);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.setString(4, otherParty);
            ps.setString(5, status);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get ALL transactions for a specific wallet (for history screen)
    public List<Transaction> getTransactionHistory(int walletId) {
        List<Transaction> list = new ArrayList<>();

        String sql = "SELECT * FROM Transaction " +
                "WHERE Wallet_ID = ? " +
                "ORDER BY Timestamp DESC";
        // newest transactions appear first

        try (Connection con = DBConnection.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, walletId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Transaction t = new Transaction(
                        rs.getInt("Trans_ID"),
                        rs.getInt("Wallet_ID"),
                        rs.getString("Type"),
                        rs.getDouble("Amount"),
                        rs.getString("Other_Party"),
                        rs.getTimestamp("Timestamp").toLocalDateTime(),
                        rs.getString("Status")
                );
                list.add(t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list; // returns empty list if no transactions found
    }

    // Get only DEPOSIT transactions
    public List<Transaction> getDeposits(int walletId) {
        return getByType(walletId, "DEPOSIT");
    }

    // Get only WITHDRAWAL transactions
    public List<Transaction> getWithdrawals(int walletId) {
        return getByType(walletId, "WITHDRAW");
    }

    // Get only TRANSFER transactions
    public List<Transaction> getTransfers(int walletId) {
        return getByType(walletId, "TRANSFER");
    }

    // Private helper - filters transactions by type
    private List<Transaction> getByType(int walletId, String type) {
        List<Transaction> list = new ArrayList<>();

        String sql = "SELECT * FROM Transaction " +
                "WHERE Wallet_ID = ? AND Type = ? " +
                "ORDER BY Timestamp DESC";

        try (Connection con = DBConnection.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, walletId);
            ps.setString(2, type);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Transaction(
                        rs.getInt("Trans_ID"),
                        rs.getInt("Wallet_ID"),
                        rs.getString("Type"),
                        rs.getDouble("Amount"),
                        rs.getString("Other_Party"),
                        rs.getTimestamp("Timestamp").toLocalDateTime(),
                        rs.getString("Status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Get total amount spent (all withdrawals + transfers)
    public double getTotalSpent(int walletId) {
        String sql = "SELECT SUM(Amount) FROM Transaction " +
                "WHERE Wallet_ID = ? " +
                "AND Type IN ('WITHDRAW', 'TRANSFER') " +
                "AND Status = 'SUCCESS'";

        try (Connection con = DBConnection.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, walletId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // Get total amount received (all deposits)
    public double getTotalReceived(int walletId) {
        String sql = "SELECT SUM(Amount) FROM Transaction " +
                "WHERE Wallet_ID = ? " +
                "AND Type = 'DEPOSIT' " +
                "AND Status = 'SUCCESS'";

        try (Connection con = DBConnection.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, walletId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    public int getTransactionCount(int walletId) {
        String sql = "SELECT COUNT(*) FROM Transaction" +
                " WHERE Wallet_ID = ? AND Status='SUCCESS'";
        try (Connection con = DBConnection.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, walletId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("getTransactionCount error: " + e.getMessage());
        }
        return 0;
    }
}