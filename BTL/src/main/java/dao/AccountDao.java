package dao;

import model.Account;
import model.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AccountDao {

    public Account getAccountByUsername(String username) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM accounts WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Account account = new Account(
                        rs.getInt("account_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getBoolean("approved"),
                        null
                    );

                    String nameQuery = "";
                    if (account.getRole().equals("teacher")) {
                        nameQuery = "SELECT name FROM teachers WHERE account_id = ?";
                    } else if (account.getRole().equals("student")) {
                        nameQuery = "SELECT name FROM students WHERE account_id = ?";
                    } else if (account.getRole().equals("admin")) {
                        account.setName("Admin");
                        return account;
                    } else {
                        System.err.println("Vai trò không hợp lệ cho username=" + username + ": " + account.getRole());
                        account.setName("Unknown");
                        return account;
                    }

                    if (!nameQuery.isEmpty()) {
                        try (PreparedStatement nameStmt = conn.prepareStatement(nameQuery)) {
                            nameStmt.setInt(1, account.getAccountId());
                            try (ResultSet nameRs = nameStmt.executeQuery()) {
                                if (nameRs.next()) {
                                    account.setName(nameRs.getString("name"));
                                } else {
                                    System.err.println("Không tìm thấy tên cho account_id=" + account.getAccountId() +
                                        " trong bảng " + (account.getRole().equals("teacher") ? "teachers" : "students"));
                                    account.setName("Unknown");
                                }
                            }
                        } catch (SQLException e) {
                            System.err.println("Lỗi khi lấy tên từ bảng " +
                                (account.getRole().equals("teacher") ? "teachers" : "students") + ": " + e.getMessage());
                            account.setName("Unknown");
                        }
                    }
                    // Bổ sung: Đảm bảo lấy tên từ getNameByAccountId nếu không lấy được từ trên
                    if (account.getName() == null || account.getName().isEmpty()) {
                        account.setName(getNameByAccountId(account.getAccountId(), account.getRole()));
                    }
                    return account;
                }
            }
        }
        return null;
    }

    public Account authenticate(String username, String password) throws SQLException, ClassNotFoundException {
        System.out.println("Bắt đầu xác thực: username=" + username + ", password=" + password);
        Account account = getAccountByUsername(username);
        if (account != null) {
            System.out.println("Tìm thấy tài khoản: username=" + account.getUsername() + ", password=" + account.getPassword() + 
                              ", approved=" + account.isApproved());
            if (account.getPassword().equals(password) && account.isApproved()) {
                System.out.println("Xác thực thành công: " + account.getUsername());
                return account;
            } else {
                System.out.println("Xác thực thất bại: Mật khẩu không khớp hoặc tài khoản chưa được duyệt!");
            }
        } else {
            System.out.println("Không tìm thấy tài khoản với username: " + username);
        }
        return null;
    }

    public boolean isEmailExists(String email) throws SQLException, ClassNotFoundException {
        String query = "SELECT COUNT(*) FROM accounts WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public int addAccount(Account account) throws SQLException, ClassNotFoundException {
        if (account == null || account.getUsername() == null || account.getUsername().trim().isEmpty() ||
            account.getEmail() == null || account.getEmail().trim().isEmpty() ||
            account.getPassword() == null || account.getPassword().trim().isEmpty() ||
            account.getRole() == null || !account.getRole().matches("admin|teacher|student")) {
            throw new IllegalArgumentException("Dữ liệu tài khoản không hợp lệ");
        }
        if (isEmailExists(account.getEmail())) {
            throw new SQLException("Email đã tồn tại: " + account.getEmail());
        }
        String query = "INSERT INTO accounts (username, password, email, role, approved) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, account.getUsername());
            stmt.setString(2, account.getPassword());
            stmt.setString(3, account.getEmail());
            stmt.setString(4, account.getRole());
            stmt.setBoolean(5, account.isApproved());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Không thể thêm tài khoản");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Trả về account_id
                } else {
                    throw new SQLException("Không thể lấy account_id sau khi tạo tài khoản");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm tài khoản: " + e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                throw new SQLException("Username hoặc email đã tồn tại: " + e.getMessage());
            }
            throw e;
        }
    }

    public void updateAccount(Account account) throws SQLException, ClassNotFoundException {
        if (account == null || account.getAccountId() <= 0 || account.getUsername() == null || 
            account.getUsername().trim().isEmpty() || account.getRole() == null) {
            throw new IllegalArgumentException("Dữ liệu tài khoản không hợp lệ");
        }
        Account existingAccount = getAccountById(account.getAccountId());
        if (existingAccount == null) {
            throw new SQLException("Không tìm thấy tài khoản với ID " + account.getAccountId());
        }
        if (!account.getEmail().equals(existingAccount.getEmail()) && isEmailExists(account.getEmail())) {
            throw new SQLException("Email đã tồn tại: " + account.getEmail());
        }
        String query = "UPDATE accounts SET username = ?, password = ?, email = ?, role = ?, approved = ? WHERE account_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, account.getUsername());
            stmt.setString(2, account.getPassword());
            stmt.setString(3, account.getEmail());
            stmt.setString(4, account.getRole());
            stmt.setBoolean(5, account.isApproved());
            stmt.setInt(6, account.getAccountId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Không thể cập nhật tài khoản");
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new SQLException("Username hoặc email đã tồn tại: " + e.getMessage());
            }
            throw e;
        }
    }

    public void deleteAccount(int accountId) throws SQLException, ClassNotFoundException {
        Account existingAccount = getAccountById(accountId);
        if (existingAccount == null) {
            throw new SQLException("Không tìm thấy tài khoản với ID " + accountId);
        }
        String query = "DELETE FROM accounts WHERE account_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Không tìm thấy tài khoản với ID " + accountId);
            }
        }
    }

    public List<Account> getAllAccounts() throws SQLException, ClassNotFoundException {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM accounts";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Account account = new Account(
                    rs.getInt("account_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("role"),
                    rs.getBoolean("approved"),
                    null
                );

                String nameQuery = "";
                if (account.getRole().equals("teacher")) {
                    nameQuery = "SELECT name FROM teachers WHERE account_id = ?";
                } else if (account.getRole().equals("student")) {
                    nameQuery = "SELECT name FROM students WHERE account_id = ?";
                } else if (account.getRole().equals("admin")) {
                    account.setName("Admin");
                }

                if (!nameQuery.isEmpty()) {
                    try (PreparedStatement nameStmt = conn.prepareStatement(nameQuery)) {
                        nameStmt.setInt(1, account.getAccountId());
                        try (ResultSet nameRs = nameStmt.executeQuery()) {
                            if (nameRs.next()) {
                                account.setName(nameRs.getString("name"));
                            } else {
                                System.err.println("Không tìm thấy tên cho account_id=" + account.getAccountId() +
                                    " trong bảng " + (account.getRole().equals("teacher") ? "teachers" : "students"));
                                account.setName("Unknown");
                            }
                        }
                    } catch (SQLException e) {
                        System.err.println("Lỗi khi lấy tên từ bảng " +
                            (account.getRole().equals("teacher") ? "teachers" : "students") + ": " + e.getMessage());
                        account.setName("Unknown");
                    }
                }
                accounts.add(account);
            }
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi lấy danh sách tài khoản: " + e.getMessage(), e);
        }
        return accounts;
    }

    public Account getAccountById(int accountId) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM accounts WHERE account_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Account account = new Account(
                        rs.getInt("account_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getBoolean("approved"),
                        null
                    );

                    String nameQuery = "";
                    if (account.getRole().equals("teacher")) {
                        nameQuery = "SELECT name FROM teachers WHERE account_id = ?";
                    } else if (account.getRole().equals("student")) {
                        nameQuery = "SELECT name FROM students WHERE account_id = ?";
                    } else if (account.getRole().equals("admin")) {
                        account.setName("Admin");
                        return account;
                    }

                    if (!nameQuery.isEmpty()) {
                        try (PreparedStatement nameStmt = conn.prepareStatement(nameQuery)) {
                            nameStmt.setInt(1, account.getAccountId());
                            try (ResultSet nameRs = nameStmt.executeQuery()) {
                                if (nameRs.next()) {
                                    account.setName(nameRs.getString("name"));
                                } else {
                                    System.err.println("Không tìm thấy tên cho account_id=" + account.getAccountId() +
                                        " trong bảng " + (account.getRole().equals("teacher") ? "teachers" : "students"));
                                    account.setName("Unknown");
                                }
                            }
                        } catch (SQLException e) {
                            System.err.println("Lỗi khi lấy tên từ bảng " +
                                (account.getRole().equals("teacher") ? "teachers" : "students") + ": " + e.getMessage());
                            account.setName("Unknown");
                        }
                    }
                    return account;
                }
            }
        }
        return null;
    }

    public boolean isAccountExists(int accountId) throws SQLException, ClassNotFoundException {
        String query = "SELECT COUNT(*) FROM accounts WHERE account_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Bổ sung: Phương thức đồng bộ account_id giữa accounts và teachers
    public void syncTeacherAccountId(int accountId, String email) throws SQLException, ClassNotFoundException {
        String checkQuery = "SELECT teacher_id FROM teachers WHERE email = ? AND (account_id IS NULL OR account_id != ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setString(1, email);
            checkStmt.setInt(2, accountId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    int teacherId = rs.getInt("teacher_id");
                    String updateQuery = "UPDATE teachers SET account_id = ? WHERE teacher_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, accountId);
                        updateStmt.setInt(2, teacherId);
                        int rowsAffected = updateStmt.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Đồng bộ account_id=" + accountId + " cho teacher_id=" + teacherId);
                        } else {
                            System.err.println("Không thể đồng bộ account_id=" + accountId + " cho teacher_id=" + teacherId);
                        }
                    }
                } else {
                    // Nếu không tìm thấy teacher với email, kiểm tra xem cần thêm mới không (tùy chọn)
                    String insertQuery = "INSERT INTO teachers (account_id, email, name) VALUES (?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                        insertStmt.setInt(1, accountId);
                        insertStmt.setString(2, email);
                        insertStmt.setString(3, "Default Teacher Name"); // Tên mặc định, có thể thay bằng logic khác
                        int rowsAffected = insertStmt.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Thêm mới teacher với account_id=" + accountId);
                        }
                    }
                }
            }
        }
    }

    // Bổ sung: Phương thức kiểm tra account_id trong bảng teachers
    public boolean verifyTeacherAccountLink(int accountId) throws SQLException, ClassNotFoundException {
        String query = "SELECT COUNT(*) FROM teachers WHERE account_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Cập nhật phương thức getNameByAccountId để sử dụng syncTeacherAccountId
    public String getNameByAccountId(int accountId, String role) throws SQLException, ClassNotFoundException {
        if (role == null) return "Unknown";
        if (role.equals("admin")) return "Admin";
        if (role.equals("student")) {
            return "Student_" + accountId;
        }
        if (role.equals("teacher")) {
            // Lấy thông tin tài khoản để đồng bộ
            Account account = getAccountById(accountId);
            if (account != null) {
                syncTeacherAccountId(accountId, account.getEmail());
            }
            // Kiểm tra liên kết account_id
            if (!verifyTeacherAccountLink(accountId)) {
                System.err.println("Không tìm thấy liên kết account_id=" + accountId + " trong bảng teachers sau khi đồng bộ");
                return "Teacher_" + accountId;
            }
            String query = "SELECT name FROM teachers WHERE account_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, accountId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("name");
                    }
                }
            }
            return "Teacher_" + accountId;
        }
        return "Unknown";
    }
}