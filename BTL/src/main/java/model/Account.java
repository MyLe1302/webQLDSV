package model;

public class Account {
    private int accountId;
    private String username;
    private String password;
    private String email;
    private String role;
    private boolean approved;
    private String name;

    // Constructor mặc định
    public Account() {
    }

    // Constructor đầy đủ (không có createdAt)
    public Account(int accountId, String username, String password, String email, String role, boolean approved, String name) {
        this.accountId = accountId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.approved = approved;
        this.name = name;
    }

    // Getters and Setters
    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}