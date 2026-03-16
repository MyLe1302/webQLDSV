package dao;

import model.Student;
import model.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentDao {

    // Các phương thức hiện có giữ nguyên, chỉ cập nhật/thêm phương thức liên quan đến điểm danh
    public List<Student> getStudentsByClassId(int classId) throws SQLException, ClassNotFoundException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT s.* FROM students s " +
                      "JOIN student_classes sc ON s.student_id = sc.student_id " +
                      "WHERE sc.class_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, classId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(createStudentFromResultSet(rs));
                }
                System.out.println("Số sinh viên lấy được cho class_id=" + classId + ": " + students.size());
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách sinh viên theo mã lớp " + classId + ": " + e.getMessage());
            throw new SQLException("Lỗi khi lấy danh sách sinh viên theo mã lớp: " + e.getMessage(), e);
        }
        return students;
    }

    public Student getStudentById(String studentId) throws SQLException, ClassNotFoundException {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sinh viên không hợp lệ");
        }
        String query = "SELECT s.* FROM students s WHERE s.student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createStudentFromResultSet(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi lấy sinh viên với mã " + studentId + ": " + e.getMessage(), e);
        }
    }

    public void addStudent(Student student) throws SQLException, ClassNotFoundException {
        if (student == null || student.getStudentId() == null || student.getStudentId().trim().isEmpty() ||
            student.getName() == null || student.getName().trim().isEmpty() ||
            student.getDepartment() == null || student.getDepartment().trim().isEmpty() ||
            student.getBirthDate() == null || student.getGender() == null ||
            !student.getGender().matches("Nam|Nữ")) {
            throw new IllegalArgumentException("Dữ liệu sinh viên không hợp lệ");
        }
        String query = "INSERT INTO students (student_id, name, department, course_id, birth_date, gender, photo, class_id, account_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getDepartment());
            pstmt.setInt(4, student.getCourseId());
            pstmt.setDate(5, new java.sql.Date(student.getBirthDate().getTime()));
            pstmt.setString(6, student.getGender());
            pstmt.setString(7, student.getPhoto());
            pstmt.setInt(8, student.getClassId());
            pstmt.setInt(9, student.getAccountId());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Không thể thêm sinh viên");
            }
            if (student.getClassId() != 0) {
                String assignQuery = "INSERT INTO student_classes (student_id, class_id) VALUES (?, ?)";
                try (PreparedStatement assignStmt = conn.prepareStatement(assignQuery)) {
                    assignStmt.setString(1, student.getStudentId());
                    assignStmt.setInt(2, student.getClassId());
                    assignStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi thêm sinh viên: " + e.getMessage(), e);
        }
    }

    public void updateStudent(Student student) throws SQLException, ClassNotFoundException {
        if (student == null || student.getStudentId() == null || student.getStudentId().trim().isEmpty() ||
            student.getName() == null || student.getName().trim().isEmpty() ||
            student.getDepartment() == null || student.getDepartment().trim().isEmpty() ||
            student.getBirthDate() == null || student.getGender() == null ||
            !student.getGender().matches("Nam|Nữ")) {
            throw new IllegalArgumentException("Dữ liệu sinh viên không hợp lệ");
        }
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            String updateQuery = "UPDATE students SET name = ?, department = ?, course_id = ?, birth_date = ?, gender = ?, photo = ?, class_id = ?, account_id = ? WHERE student_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                pstmt.setString(1, student.getName());
                pstmt.setString(2, student.getDepartment());
                pstmt.setInt(3, student.getCourseId());
                pstmt.setDate(4, new java.sql.Date(student.getBirthDate().getTime()));
                pstmt.setString(5, student.getGender());
                pstmt.setString(6, student.getPhoto());
                pstmt.setInt(7, student.getClassId());
                pstmt.setInt(8, student.getAccountId());
                pstmt.setString(9, student.getStudentId());
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Không tìm thấy sinh viên với mã " + student.getStudentId());
                }
            }
            String deleteClassQuery = "DELETE FROM student_classes WHERE student_id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteClassQuery)) {
                deleteStmt.setString(1, student.getStudentId());
                deleteStmt.executeUpdate();
            }
            if (student.getClassId() != 0) {
                String assignQuery = "INSERT INTO student_classes (student_id, class_id) VALUES (?, ?)";
                try (PreparedStatement assignStmt = conn.prepareStatement(assignQuery)) {
                    assignStmt.setString(1, student.getStudentId());
                    assignStmt.setInt(2, student.getClassId());
                    assignStmt.executeUpdate();
                }
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new SQLException("Lỗi khi cập nhật sinh viên: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void deleteStudent(String studentId) throws SQLException, ClassNotFoundException {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sinh viên không hợp lệ");
        }
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            String deleteClassQuery = "DELETE FROM student_classes WHERE student_id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteClassQuery)) {
                deleteStmt.setString(1, studentId);
                deleteStmt.executeUpdate();
            }
            String deleteStudentQuery = "DELETE FROM students WHERE student_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteStudentQuery)) {
                pstmt.setString(1, studentId);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Không tìm thấy sinh viên với mã " + studentId);
                }
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new SQLException("Lỗi khi xóa sinh viên: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public List<Student> getAllStudents() throws SQLException, ClassNotFoundException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT s.* FROM students s";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                students.add(createStudentFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi lấy danh sách tất cả sinh viên: " + e.getMessage(), e);
        }
        return students;
    }

    public String getClassNameById(int classId) throws SQLException, ClassNotFoundException {
        String query = "SELECT class_name FROM classes WHERE class_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, classId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("class_name");
                }
                return null;
            }
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi lấy tên lớp với mã " + classId + ": " + e.getMessage(), e);
        }
    }

    private Student createStudentFromResultSet(ResultSet rs) throws SQLException {
        Student student = new Student(
            rs.getString("student_id"),
            rs.getString("name"),
            rs.getString("department"),
            rs.getInt("course_id"),
            rs.getDate("birth_date"),
            rs.getString("gender"),
            rs.getString("photo"),
            rs.getInt("class_id"),
            rs.getInt("account_id")
        );
        return student;
    }

    // Cập nhật phương thức saveAttendance để bỏ attendance_date
    public void saveAttendance(int classId, String studentId, int sessionNumber, String status, String note) throws SQLException, ClassNotFoundException {
        if (studentId == null || studentId.trim().isEmpty() || status == null || !status.matches("present|absent_without_permission|absent_with_permission")) {
            throw new IllegalArgumentException("Dữ liệu điểm danh không hợp lệ");
        }
        // Kiểm tra xem bản ghi đã tồn tại
        String checkQuery = "SELECT COUNT(*) FROM attendance_records WHERE class_id = ? AND student_id = ? AND session_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, classId);
            checkStmt.setString(2, studentId);
            checkStmt.setInt(3, sessionNumber);
            try (ResultSet rs = checkStmt.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    // Cập nhật bản ghi
                    String updateQuery = "UPDATE attendance_records SET status = ?, note = ? WHERE class_id = ? AND student_id = ? AND session_number = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, status);
                        updateStmt.setString(2, note != null ? note : "");
                        updateStmt.setInt(3, classId);
                        updateStmt.setString(4, studentId);
                        updateStmt.setInt(5, sessionNumber);
                        int rowsAffected = updateStmt.executeUpdate();
                        if (rowsAffected == 0) {
                            throw new SQLException("Không thể cập nhật điểm danh cho sinh viên " + studentId);
                        }
                    }
                } else {
                    // Thêm bản ghi mới
                    String insertQuery = "INSERT INTO attendance_records (class_id, student_id, session_number, status, note) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setInt(1, classId);
                        insertStmt.setString(2, studentId);
                        insertStmt.setInt(3, sessionNumber);
                        insertStmt.setString(4, status);
                        insertStmt.setString(5, note != null ? note : "");
                        int rowsAffected = insertStmt.executeUpdate();
                        if (rowsAffected == 0) {
                            throw new SQLException("Không thể lưu điểm danh cho sinh viên " + studentId);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi lưu hoặc cập nhật điểm danh: " + e.getMessage(), e);
        }
    }

    // Thêm phương thức để lấy thông tin điểm danh đã lưu
    public Map<String, Map<String, String>> getAttendanceForSession(int classId, int sessionNumber) throws SQLException, ClassNotFoundException {
        Map<String, Map<String, String>> attendanceData = new HashMap<>();
        String query = "SELECT student_id, status, note FROM attendance_records WHERE class_id = ? AND session_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, classId);
            stmt.setInt(2, sessionNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> record = new HashMap<>();
                    record.put("status", rs.getString("status"));
                    record.put("note", rs.getString("note") != null ? rs.getString("note") : "");
                    attendanceData.put(rs.getString("student_id"), record);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi lấy dữ liệu điểm danh: " + e.getMessage(), e);
        }
        return attendanceData;
    }

    public int getTotalAbsences(String studentId, int classId) throws SQLException, ClassNotFoundException {
        String query = "SELECT COUNT(*) FROM attendance_records WHERE student_id = ? AND class_id = ? AND status IN ('absent_without_permission', 'absent_with_permission')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentId);
            stmt.setInt(2, classId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi lấy tổng số buổi nghỉ: " + e.getMessage(), e);
        }
    }
}