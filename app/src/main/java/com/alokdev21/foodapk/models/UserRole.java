package com.alokdev21.foodapk.models;

public class UserRole {
    private String email;
    private String role;

    // No-argument constructor for Firestore deserialization
    public UserRole() {}

    public UserRole(String email, String role) {
        this.email = email;
        this.role = role;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
