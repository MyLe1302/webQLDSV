package model;

import java.sql.Timestamp;

public class Contact {
    private int contactId;
    private String name;
    private String email;
    private String message;
    private Timestamp createdAt;

    public Contact(int contactId, String name, String email, String message, Timestamp createdAt) {
        this.contactId = contactId;
        this.name = name;
        this.email = email;
        this.message = message;
        this.createdAt = createdAt;
    }

    public int getContactId() { return contactId; }
    public void setContactId(int contactId) { this.contactId = contactId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}