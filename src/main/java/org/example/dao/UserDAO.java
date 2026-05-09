package org.example.dao;
import org.example.db.DBConnection;
import org.example.models.User;
import java.sql.*;

public class UserDAO {

    // Save new user to database
    public boolean registerUser(String username,
                                String email,
                                String passwordHash) {
        String sql = "INSERT INTO User " +
                "(Username, Email, Password_Hash) " +
                "VALUES (?, ?, ?)";
        try (Connection con = DBConnection.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, passwordHash);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Find user by username (used during login)
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM User WHERE Username = ?";
        try (Connection con = DBConnection.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("User_ID"),
                        rs.getString("Username"),
                        rs.getString("Email"),
                        rs.getString("Password_Hash")
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }// Find user by ID
    public User getUserById(int userId) {
        String sql = "SELECT * FROM User WHERE User_ID = ?";
        try (Connection con = DBConnection.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("User_ID"),
                        rs.getString("Username"),
                        rs.getString("Email"),
                        rs.getString("Password_Hash")
                );
            }
        } catch (SQLException e) {
            System.err.println("getUserById error: " + e.getMessage());
        }
        return null;
    }

    //  Check if username already taken
    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM User WHERE Username = ?";
        try (Connection con = DBConnection.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.err.println("usernameExists error: " + e.getMessage());
        }
        return false;
    }
    // ── Check if email already taken
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM User WHERE Email = ?";
        try (Connection con = DBConnection.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.err.println("emailExists error: " + e.getMessage());
        }
        return false;
    }
}