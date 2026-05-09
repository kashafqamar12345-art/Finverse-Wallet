package org.example.dao;

import org.example.db.DBConnection;
import org.example.models.Wallet;
import java.sql.*;

public class WalletDAO {

    public boolean createWallet(int userId) {
        String sql = "INSERT INTO Wallet (User_ID, Balance, Currency)" +
                " VALUES (?, 0.00, 'PKR')";
        try (Connection con = DBConnection.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public Wallet getWalletByUserId(int userId) {
        String sql = "SELECT * FROM Wallet WHERE User_ID = ?";
        try (Connection con = DBConnection.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Wallet(
                        rs.getInt("Wallet_ID"),
                        rs.getInt("User_ID"),
                        rs.getDouble("Balance"),
                        rs.getString("Currency")
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public int getUserIdByWalletId(int walletId) {
        String sql = "SELECT User_ID FROM Wallet WHERE Wallet_ID = ?";
        try (Connection con = DBConnection.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, walletId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("User_ID");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    // ── Get Wallet_ID from username
    public int getWalletIdByUsername(String username) {
        String sql = "SELECT w.Wallet_ID FROM Wallet w " +
                "JOIN User u ON w.User_ID = u.User_ID " +
                "WHERE u.Username = ?";
        try (Connection con = DBConnection.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("Wallet_ID");

        } catch (SQLException e) {
            System.err.println("getWalletIdByUsername error: " + e.getMessage());
        }
        return -1;
    }
    // for updating balance
    public boolean updateBalance(int walletId, double newBalance) {
        String sql = "UPDATE Wallet SET Balance = ? WHERE Wallet_ID = ?";
        try (Connection con = DBConnection.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, newBalance);
            ps.setInt(2, walletId);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // THE ATOMIC TRANSFER
    public boolean transfer(int fromWalletId,
                            int toWalletId,
                            double amount) {
        Connection con = null;
        try {
            con = DBConnection.connect();
            con.setAutoCommit(false); // START TRANSACTION

            // Deduct from sender
            String deduct = "UPDATE Wallet SET Balance = Balance - ?" +
                    " WHERE Wallet_ID = ?";
            PreparedStatement ps1 = con.prepareStatement(deduct);
            ps1.setDouble(1, amount);
            ps1.setInt(2, fromWalletId);
            ps1.executeUpdate();

            // Add to receiver
            String add = "UPDATE Wallet SET Balance = Balance + ?" +
                    " WHERE Wallet_ID = ?";
            PreparedStatement ps2 = con.prepareStatement(add);
            ps2.setDouble(1, amount);
            ps2.setInt(2, toWalletId);
            ps2.executeUpdate();

            con.commit(); // SUCCESS - make permanent
            return true;

        } catch (Exception e) {
            try { if(con != null) con.rollback(); } // FAIL - undo all
            catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        }
    }
}