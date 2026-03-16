package dao;

import model.DatabaseConnection;
import model.Subject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubjectDao {

    // Lấy tất cả môn học (cho admin)
    public List<Subject> getAllSubjects() throws SQLException, ClassNotFoundException {
        List<Subject> subjects = new ArrayList<>();
        String query = "SELECT subject_id, subject_name, credits FROM subjects";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                subjects.add(new Subject(
                    rs.getString("subject_id"),
                    rs.getString("subject_name"),
                    rs.getInt("credits")
                ));
            }
            System.out.println("Số môn học tìm thấy: " + subjects.size());
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy danh sách môn học: " + e.getMessage());
            throw e;
        }
        return subjects;
    }

    // Lấy môn học theo subject_id
    public Subject getSubjectById(String subjectId) throws SQLException, ClassNotFoundException {
        String query = "SELECT subject_id, subject_name, credits FROM subjects WHERE subject_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Subject(
                        rs.getString("subject_id"),
                        rs.getString("subject_name"),
                        rs.getInt("credits")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy môn học theo ID: " + e.getMessage());
            throw e;
        }
        return null;
    }

    // Lấy danh sách môn học theo account_id và vai trò (cho teacher/student)
    public List<Subject> getSubjectsByAccount(int accountId, String role) throws SQLException, ClassNotFoundException {
        List<Subject> subjects = new ArrayList<>();
        String query;
        if ("teacher".equalsIgnoreCase(role)) {
            query = "SELECT DISTINCT s.subject_id, s.subject_name, s.credits " +
                    "FROM subjects s " +
                    "JOIN classes c ON s.subject_id = c.subject_id " +
                    "JOIN teachers t ON c.teacher_id = t.teacher_id " +
                    "WHERE t.account_id = ?";
        } else if ("student".equalsIgnoreCase(role)) {
            query = "SELECT DISTINCT s.subject_id, s.subject_name, s.credits " +
                    "FROM subjects s " +
                    "JOIN classes c ON s.subject_id = c.subject_id " +
                    "JOIN students st ON c.class_id = st.class_id " +
                    "WHERE st.account_id = ?";
        } else {
            throw new IllegalArgumentException("Vai trò không hợp lệ: " + role);
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    subjects.add(new Subject(
                        rs.getString("subject_id"),
                        rs.getString("subject_name"),
                        rs.getInt("credits")
                    ));
                }
                System.out.println("Số môn học tìm thấy cho account_id=" + accountId + ", role=" + role + ": " + subjects.size());
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy môn học theo account_id: " + e.getMessage());
            throw e;
        }
        return subjects;
    }

    // Thêm môn học
    public void addSubject(Subject subject) throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO subjects (subject_id, subject_name, credits) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, subject.getSubjectId());
            ps.setString(2, subject.getSubjectName());
            ps.setInt(3, subject.getCredits() != null ? subject.getCredits() : 0);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Không thể thêm môn học.");
            }
            System.out.println("Thêm môn học thành công: " + subject.getSubjectId());
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi thêm môn học: " + e.getMessage());
            throw new SQLException(getDetailedErrorMessage(e, "thêm môn học"), e);
        }
    }

    // Sửa môn học
    public void updateSubject(Subject subject) throws SQLException, ClassNotFoundException {
        String query = "UPDATE subjects SET subject_name = ?, credits = ? WHERE subject_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, subject.getSubjectName());
            ps.setInt(2, subject.getCredits() != null ? subject.getCredits() : 0);
            ps.setString(3, subject.getSubjectId());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Không tìm thấy môn học với mã: " + subject.getSubjectId());
            }
            System.out.println("Cập nhật môn học thành công: " + subject.getSubjectId());
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi cập nhật môn học: " + e.getMessage());
            throw new SQLException(getDetailedErrorMessage(e, "cập nhật môn học"), e);
        }
    }

    // Xóa môn học
    public void deleteSubject(String subjectId) throws SQLException, ClassNotFoundException {
        String query = "DELETE FROM subjects WHERE subject_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, subjectId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Không tìm thấy môn học với mã: " + subjectId);
            }
            System.out.println("Xóa môn học thành công: " + subjectId);
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi xóa môn học: " + e.getMessage());
            throw new SQLException(getDetailedErrorMessage(e, "xóa môn học"), e);
        }
    }

    // Tạo thông báo lỗi chi tiết
    private String getDetailedErrorMessage(SQLException e, String action) {
        String message = "Lỗi khi " + action + ": " + e.getMessage();
        if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
            message = "Lỗi khi " + action + ": Vi phạm ràng buộc cơ sở dữ liệu (môn học có thể đang được sử dụng).";
        }
        return message;
    }
}