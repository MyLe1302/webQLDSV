package dao;

import model.Contact;
import model.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContactDao {
    public void addContact(Contact contact) throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO contacts (name, email, message) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, contact.getName());
            stmt.setString(2, contact.getEmail());
            stmt.setString(3, contact.getMessage());
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Thêm liên hệ thành công, số hàng ảnh hưởng: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi thêm liên hệ: " + e.getMessage());
            throw e;
        }
    }

    public List<Contact> getAllContacts() throws SQLException, ClassNotFoundException {
        List<Contact> contacts = new ArrayList<>();
        String query = "SELECT * FROM contacts ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Contact contact = new Contact(
                    rs.getInt("contact_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("message"),
                    rs.getTimestamp("created_at")
                );
                contacts.add(contact);
            }
            System.out.println("Số liên hệ tìm thấy: " + contacts.size());
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy danh sách liên hệ: " + e.getMessage());
            throw e;
        }
        return contacts;
    }
}