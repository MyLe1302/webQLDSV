package dao;

import model.Teacher;
import model.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAO {
    // Kiểm tra xem teacher_id đã tồn tại chưa
    public boolean isTeacherIdExists(int teacherId) throws SQLException, ClassNotFoundException {
        String query = "SELECT COUNT(*) FROM teachers WHERE teacher_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, teacherId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    public List<Teacher> getAllTeachers() throws SQLException, ClassNotFoundException {
        List<Teacher> teachers = new ArrayList<>();
        String query = "SELECT * FROM teachers";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("Executing getAllTeachers query");
            while (rs.next()) {
                Teacher teacher = new Teacher();
                teacher.setTeacherId(rs.getInt("teacher_id"));
                teacher.setName(rs.getString("name"));
                teacher.setEmail(rs.getString("email"));
                teacher.setPhone(rs.getString("phone"));
                teacher.setAccountId(rs.getInt("account_id"));
                teachers.add(teacher);
            }
            System.out.println("Retrieved " + teachers.size() + " teachers");
        } catch (SQLException e) {
            System.err.println("SQLException in getAllTeachers: " + e.getMessage());
            throw e;
        }
        return teachers;
    }

    public void addTeacher(Teacher teacher) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // Thêm bản ghi vào bảng accounts
            String insertAccount = "INSERT INTO accounts (username, password, email, role, approved) VALUES (?, ?, ?, ?, ?)";
            int accountId;
            try (PreparedStatement pstmt = conn.prepareStatement(insertAccount, Statement.RETURN_GENERATED_KEYS)) {
                String username = teacher.getEmail() != null && !teacher.getEmail().isEmpty() ? teacher.getEmail() : "teacher_" + System.currentTimeMillis();
                String email = teacher.getEmail() != null && !teacher.getEmail().isEmpty() ? teacher.getEmail() : "default_" + System.currentTimeMillis() + "@example.com";
                String defaultPassword = "defaultPassword123"; // Nên mã hóa trong thực tế
                pstmt.setString(1, username);
                pstmt.setString(2, defaultPassword);
                pstmt.setString(3, email);
                pstmt.setString(4, "teacher");
                pstmt.setInt(5, 0); // approved mặc định là 0
                pstmt.executeUpdate();

                // Lấy account_id tự động tăng
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    accountId = rs.getInt(1);
                    teacher.setAccountId(accountId);
                    System.out.println("Added account with ID: " + accountId + " for teacher");
                } else {
                    throw new SQLException("Không thể lấy account_id từ bảng accounts!");
                }
            }

            // Thêm giảng viên vào bảng teachers với account_id
            String insertTeacher = "INSERT INTO teachers (name, email, phone, account_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertTeacher, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, teacher.getName());
                pstmt.setString(2, teacher.getEmail());
                pstmt.setString(3, teacher.getPhone());
                pstmt.setInt(4, accountId);
                pstmt.executeUpdate();

                // Lấy teacher_id tự động tăng
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    teacher.setTeacherId(rs.getInt(1));
                    System.out.println("Added teacher with ID: " + teacher.getTeacherId());
                }
            }

            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback nếu có lỗi
                    System.err.println("Transaction rolled back due to: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    public void updateTeacher(Teacher teacher) throws SQLException, ClassNotFoundException {
        String query = "UPDATE teachers SET name = ?, email = ?, phone = ? WHERE teacher_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, teacher.getName());
            pstmt.setString(2, teacher.getEmail());
            pstmt.setString(3, teacher.getPhone());
            pstmt.setInt(4, teacher.getTeacherId());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Không tìm thấy giảng viên với mã: " + teacher.getTeacherId());
            }
        }
    }

    public void deleteTeacher(int teacherId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // Log teacherId để debug
            System.out.println("Attempting to delete teacher with ID: " + teacherId);

            // Kiểm tra xem teacher_id có tồn tại không
            if (!isTeacherIdExists(teacherId)) {
                throw new SQLException("Không tìm thấy giảng viên với mã: " + teacherId);
            }

            // Xóa các bản ghi trong bảng classes liên quan đến teacher_id
            String deleteClasses = "DELETE FROM classes WHERE teacher_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteClasses)) {
                pstmt.setInt(1, teacherId);
                int rowsAffected = pstmt.executeUpdate();
                System.out.println("Deleted " + rowsAffected + " related classes for teacher ID: " + teacherId);
            }

            // Xóa giảng viên từ bảng teachers
            String deleteTeacher = "DELETE FROM teachers WHERE teacher_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteTeacher)) {
                pstmt.setInt(1, teacherId);
                int rowsAffected = pstmt.executeUpdate();
                System.out.println("Deleted teacher with ID: " + teacherId + ", Rows affected: " + rowsAffected);
            }

            // accounts sẽ được xóa tự động nhờ ON DELETE CASCADE
            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback nếu có lỗi
                    System.err.println("Transaction rolled back due to: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
}