package org.example.models;

import java.time.LocalDateTime;

public class User {
    private int userId;
    private String username;
    private String email;
    private String passwordHash;
    private LocalDateTime createdAt;


    public User(int userId, String username,
                String email, String passwordHash) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }


    public int getUserId()        { return userId; }
    public String getUsername()   { return username; }
    public String getEmail()      { return email; }
    public String getPasswordHash(){ return passwordHash; }
}
