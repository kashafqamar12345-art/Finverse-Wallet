package org.example.services;

import org.example.dao.UserDAO;
import org.example.dao.WalletDAO;
import org.example.models.User;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

    private final UserDAO  userDAO  = new UserDAO();
    private final WalletDAO walletDAO = new WalletDAO();

  //Register a new User
    public String register(String username, String email, String plainPassword) {

        // ── Input validation
        if (username == null || username.trim().length() < 3) {
            return "ERROR: Username must be at least 3 characters.";
        }
        if (email == null || !email.contains("@")) {
            return "ERROR: Please enter a valid email address.";
        }
        if (plainPassword == null || plainPassword.length() < 6) {
            return "ERROR: Password must be at least 6 characters.";
        }

        // ── Check for duplicate username
        User existing = userDAO.getUserByUsername(username.trim());
        if (existing != null) {
            return "ERROR: Username '" + username + "' is already taken.";
        }

        // ── Hash password and save
        String hashed = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        boolean saved = userDAO.registerUser(username.trim(), email.trim(), hashed);

        if (!saved) {
            return "ERROR: Registration failed. Please try again.";
        }

        // ── Auto-create wallet for the new user
        User newUser = userDAO.getUserByUsername(username.trim());
        if (newUser == null) {
            return "ERROR: User saved but could not be retrieved. Contact support.";
        }

        boolean walletCreated = walletDAO.createWallet(newUser.getUserId());
        if (!walletCreated) {
            // User is created but wallet failed — still let them log in,
            // the wallet can be created on first login if needed.
            System.err.println("[AuthService] Warning: wallet creation failed for userId="
                    + newUser.getUserId());
        }

        return "SUCCESS";
    }

    public User login(String username, String plainPassword) {
        if (username == null || plainPassword == null) return null;

        User user = userDAO.getUserByUsername(username.trim());
        if (user == null) return null;

        // BCrypt comparison
        if (BCrypt.checkpw(plainPassword, user.getPasswordHash())) {
            return user;
        }
        return null;
    }
}