package org.example.utils;

import org.example.models.User;
import org.example.models.Wallet;

public class SessionManager {
    private static User currentUser;
    private static Wallet currentWallet;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    public static User getCurrentUser() {
        return currentUser;
    }
    public static void setCurrentWallet(Wallet wallet) {
        currentWallet = wallet;
    }
    public static Wallet getCurrentWallet() {
        return currentWallet;
    }
    public static void logout() {
        currentUser = null;
        currentWallet = null;
    }
}