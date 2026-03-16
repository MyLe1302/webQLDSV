package dao;

import model.Account;
import model.ql_class;
import model.Subject;
import model.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClassDAO {

    public List<ql_class> getAllClasses(Account account) throws SQLException, ClassNotFoundException {
        if (account == null || account.getRole() == null) {
            throw new IllegalArgumentException("Tài khoản không hợp lệ");
        }
        List<ql_class> classes = new ArrayList<>();
        String query;

        if (account.getRole().equals("admin")) {
            query = "SELECT c.class_id, c.class_name, c.subject_id, s.subject_name AS subject_name, c.teacher_id, t.name AS lecturer_name " +
                    "FROM classes c LEFT JOIN teachers t ON c.teacher_id = t.teacher_id " +
                    "LEFT JOIN subjects s ON c.subject_id = s.subject_id";
        } else if (account.getRole().equals("teacher")) {
            query = "SELECT c.class_id, c.class_name, c.subject_id, s.subject_name AS subject_name, c.teacher_id, t.name AS lecturer_name " +
                    "FROM classes c " +
                    "JOIN teachers t ON c.teacher_id = t.teacher_id " +
                    "LEFT JOIN subjects s ON c.subject_id = s.subject_id " +
                    "WHERE t.account_id = ?";
        } else if (account.getRole().equals("student")) {
            query = "SELECT DISTINCT c.class_id, c.class_name, c.subject_id, s.subject_name AS subject_name, c.teacher_id, t.name AS lecturer_name " +
                    "FROM classes c " +
                    "JOIN student_classes sc ON c.class_id = sc.class_id " +
                    "JOIN students st ON sc.student_id = st.student_id " +
                    "JOIN teachers t ON c.teacher_id = t.teacher_id " +
                    "LEFT JOIN subjects s ON c.subject_id = s.subject_id " +
                    "WHERE st.account_id = ?";
        } else {
            throw new IllegalArgumentException("Vai trò không hợp lệ: " + account.getRole());
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            if (!account.getRole().equals("admin")) {
                stmt.setInt(1, account.getAccountId());
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ql_class classObj = createClassFromResultSet(rs);
                    classObj.setAllSubjects(getAllSubjectsForClass(classObj.getClassId()));
                    classes.add(classObj);
                }
                System.out.println("Số lớp học trả về cho account_id=" + account.getAccountId() + ", role=" + account.getRole() + ": " + classes.size());
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách lớp học cho account_id=" + account.getAccountId() + ": " + e.getMessage());
            throw new SQLException("Lỗi khi lấy danh sách lớp học: " + e.getMessage(), e);
        }
        return classes;
    }

    public List<ql_class> getAllClasses() throws SQLException, ClassNotFoundException {
        List<ql_class> classes = new ArrayList<>();
        String query = "SELECT c.class_id, c.class_name, c.subject_id, s.subject_name AS subject_name, c.teacher_id, t.name AS lecturer_name " +
                      "FROM classes c LEFT JOIN teachers t ON c.teacher_id = t.teacher_id " +
                      "LEFT JOIN subjects s ON c.subject_id = s.subject_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ql_class classObj = createClassFromResultSet(rs);
                classObj.setAllSubjects(getAllSubjectsForClass(classObj.getClassId()));
                classes.add(classObj);
            }
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi lấy danh sách tất cả lớp học: " + e.getMessage(), e);
        }
        return classes;
    }

    public void addClass(ql_class classObj) throws SQLException, ClassNotFoundException {
        if (classObj == null || classObj.getClassName() == null || classObj.getClassName().trim().isEmpty() ||
            classObj.getSubjectId() == null || classObj.getSubjectId().trim().isEmpty()) {
            throw new IllegalArgumentException("Dữ liệu lớp học không hợp lệ");
        }
        String query = "INSERT INTO classes (class_name, subject_id, teacher_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, classObj.getClassName());
            stmt.setString(2, classObj.getSubjectId());
            if (classObj.getTeacherId() != null && !classObj.getTeacherId().trim().isEmpty()) {
                stmt.setInt(3, Integer.parseInt(classObj.getTeacherId()));
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Không thể thêm lớp học");
            }
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi thêm lớp học: " + e.getMessage(), e);
        }
    }

    public void updateClass(ql_class classObj) throws SQLException, ClassNotFoundException {
        if (classObj == null || classObj.getClassId() == null || classObj.getClassId().trim().isEmpty() ||
            classObj.getClassName() == null || classObj.getClassName().trim().isEmpty() ||
            classObj.getSubjectId() == null || classObj.getSubjectId().trim().isEmpty()) {
            throw new IllegalArgumentException("Dữ liệu lớp học không hợp lệ");
        }
        String query = "UPDATE classes SET class_name = ?, subject_id = ?, teacher_id = ? WHERE class_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, classObj.getClassName());
            stmt.setString(2, classObj.getSubjectId());
            if (classObj.getTeacherId() != null && !classObj.getTeacherId().trim().isEmpty()) {
                stmt.setInt(3, Integer.parseInt(classObj.getTeacherId()));
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            stmt.setInt(4, Integer.parseInt(classObj.getClassId()));
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Không tìm thấy lớp học với mã " + classObj.getClassId());
            }
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi cập nhật lớp học: " + e.getMessage(), e);
        }
    }

    public void deleteClass(String classId) throws SQLException, ClassNotFoundException {
        if (classId == null || classId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã lớp không hợp lệ");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Delete related data in class_subjects
            String deleteClassSubjectsQuery = "DELETE FROM class_subjects WHERE class_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteClassSubjectsQuery)) {
                stmt.setInt(1, Integer.parseInt(classId));
                stmt.executeUpdate();
            }

            // Delete related data in student_classes
            String deleteStudentClassesQuery = "DELETE FROM student_classes WHERE class_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteStudentClassesQuery)) {
                stmt.setInt(1, Integer.parseInt(classId));
                stmt.executeUpdate();
            }

            // Delete the class
            String deleteClassQuery = "DELETE FROM classes WHERE class_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteClassQuery)) {
                stmt.setInt(1, Integer.parseInt(classId));
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Không tìm thấy lớp học với mã " + classId);
                }
            }

            conn.commit();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Mã lớp không phải là số hợp lệ: " + classId, e);
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new SQLException("Lỗi khi xóa lớp học: " + e.getMessage(), e);
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

    public void assignSubjectToClass(String classId, String subjectId) throws SQLException, ClassNotFoundException {
        if (classId == null || classId.trim().isEmpty() || subjectId == null || subjectId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã lớp hoặc mã môn học không hợp lệ");
        }

        // Check if class and subject exist
        String checkClassQuery = "SELECT COUNT(*) FROM classes WHERE class_id = ?";
        String checkSubjectQuery = "SELECT COUNT(*) FROM subjects WHERE subject_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkClassStmt = conn.prepareStatement(checkClassQuery);
             PreparedStatement checkSubjectStmt = conn.prepareStatement(checkSubjectQuery)) {
            checkClassStmt.setInt(1, Integer.parseInt(classId));
            checkSubjectStmt.setString(1, subjectId);
            try (ResultSet rsClass = checkClassStmt.executeQuery();
                 ResultSet rsSubject = checkSubjectStmt.executeQuery()) {
                rsClass.next();
                rsSubject.next();
                if (rsClass.getInt(1) == 0) {
                    throw new SQLException("Lớp học không tồn tại: " + classId);
                }
                if (rsSubject.getInt(1) == 0) {
                    throw new SQLException("Môn học không tồn tại: " + subjectId);
                }
            }
        }

        // Check if subject is already assigned in class_subjects
        String checkQuery = "SELECT COUNT(*) FROM class_subjects WHERE class_id = ? AND subject_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, Integer.parseInt(classId));
            checkStmt.setString(2, subjectId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    throw new SQLException("Môn học đã được gán cho lớp này trong class_subjects!");
                }
            }
        }

        // Check if subject matches the main subject_id in classes
        String checkMainSubjectQuery = "SELECT subject_id FROM classes WHERE class_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkMainSubjectQuery)) {
            checkStmt.setInt(1, Integer.parseInt(classId));
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && subjectId.equals(rs.getString("subject_id"))) {
                    throw new SQLException("Môn học đã được gán làm môn học chính cho lớp này!");
                }
            }
        }

        // Assign subject to class in class_subjects
        String query = "INSERT INTO class_subjects (class_id, subject_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(classId));
            stmt.setString(2, subjectId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Không thể gán môn học vào lớp");
            }
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi gán môn học: " + e.getMessage(), e);
        }
    }

    public void removeSubjectFromClass(String classId, String subjectId) throws SQLException, ClassNotFoundException {
        if (classId == null || classId.trim().isEmpty() || subjectId == null || subjectId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã lớp hoặc mã môn học không hợp lệ");
        }

        // Check if class and subject exist
        String checkClassQuery = "SELECT COUNT(*) FROM classes WHERE class_id = ?";
        String checkSubjectQuery = "SELECT COUNT(*) FROM subjects WHERE subject_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkClassStmt = conn.prepareStatement(checkClassQuery);
             PreparedStatement checkSubjectStmt = conn.prepareStatement(checkSubjectQuery)) {
            checkClassStmt.setInt(1, Integer.parseInt(classId));
            checkSubjectStmt.setString(1, subjectId);
            try (ResultSet rsClass = checkClassStmt.executeQuery();
                 ResultSet rsSubject = checkSubjectStmt.executeQuery()) {
                rsClass.next();
                rsSubject.next();
                if (rsClass.getInt(1) == 0) {
                    throw new SQLException("Lớp học không tồn tại: " + classId);
                }
                if (rsSubject.getInt(1) == 0) {
                    throw new SQLException("Môn học không tồn tại: " + subjectId);
                }
            }
        }

        // Check if subject is the main subject in classes
        String checkMainSubjectQuery = "SELECT subject_id FROM classes WHERE class_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkMainSubjectQuery)) {
            checkStmt.setInt(1, Integer.parseInt(classId));
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && subjectId.equals(rs.getString("subject_id"))) {
                    throw new SQLException("Không thể xóa môn học chính của lớp!");
                }
            }
        }

        // Remove subject from class_subjects
        String query = "DELETE FROM class_subjects WHERE class_id = ? AND subject_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(classId));
            stmt.setString(2, subjectId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Môn học không được gán cho lớp này trong class_subjects!");
            }
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi xóa môn học: " + e.getMessage(), e);
        }
    }

    public List<Subject> getSubjectsForClass(String classId) throws SQLException, ClassNotFoundException {
        if (classId == null || classId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã lớp không hợp lệ");
        }

        List<Subject> subjects = new ArrayList<>();
        // Get main subject from classes
        String mainSubjectQuery = "SELECT s.subject_id, s.subject_name FROM classes c LEFT JOIN subjects s ON c.subject_id = s.subject_id WHERE c.class_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(mainSubjectQuery)) {
            stmt.setInt(1, Integer.parseInt(classId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getString("subject_id") != null) {
                    subjects.add(new Subject(rs.getString("subject_id"), rs.getString("subject_name")));
                }
            }
        }

        // Get additional subjects from class_subjects
        String secondarySubjectQuery = "SELECT s.subject_id, s.subject_name FROM class_subjects cs JOIN subjects s ON cs.subject_id = s.subject_id WHERE cs.class_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(secondarySubjectQuery)) {
            stmt.setInt(1, Integer.parseInt(classId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String subjectId = rs.getString("subject_id");
                    String subjectName = rs.getString("subject_name");
                    // Avoid duplicate subjects
                    if (subjects.stream().noneMatch(s -> s.getSubjectId().equals(subjectId))) {
                        subjects.add(new Subject(subjectId, subjectName));
                    }
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi lấy danh sách môn học cho lớp " + classId + ": " + e.getMessage(), e);
        }
        return subjects;
    }

    public List<String> getAllSubjectsForClass(String classId) throws SQLException, ClassNotFoundException {
        List<String> subjectNames = new ArrayList<>();
        // Get main subject from classes.subject_id
        String mainSubjectQuery = "SELECT s.subject_name FROM classes c LEFT JOIN subjects s ON c.subject_id = s.subject_id WHERE c.class_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(mainSubjectQuery)) {
            stmt.setInt(1, Integer.parseInt(classId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getString("subject_name") != null) {
                    subjectNames.add(rs.getString("subject_name"));
                }
            }
        }

        // Get additional subjects from class_subjects
        String secondarySubjectQuery = "SELECT s.subject_name FROM class_subjects cs JOIN subjects s ON cs.subject_id = s.subject_id WHERE cs.class_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(secondarySubjectQuery)) {
            stmt.setInt(1, Integer.parseInt(classId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String subjectName = rs.getString("subject_name");
                    if (!subjectNames.contains(subjectName)) {
                        subjectNames.add(subjectName);
                    }
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi lấy danh sách môn học cho lớp " + classId + ": " + e.getMessage(), e);
        }
        return subjectNames;
    }

    public ql_class getClassById(String classId) throws SQLException, ClassNotFoundException {
        if (classId == null || classId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã lớp không hợp lệ");
        }
        String query = "SELECT c.class_id, c.class_name, c.subject_id, s.subject_name AS subject_name, c.teacher_id, t.name AS lecturer_name " +
                      "FROM classes c LEFT JOIN teachers t ON c.teacher_id = t.teacher_id " +
                      "LEFT JOIN subjects s ON c.subject_id = s.subject_id WHERE c.class_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(classId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ql_class classObj = createClassFromResultSet(rs);
                    classObj.setAllSubjects(getAllSubjectsForClass(classId));
                    return classObj;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi lấy lớp học với mã " + classId + ": " + e.getMessage(), e);
        }
    }

    private ql_class createClassFromResultSet(ResultSet rs) throws SQLException {
        return new ql_class(
            String.valueOf(rs.getInt("class_id")),
            rs.getString("class_name"),
            rs.getString("subject_id"),
            rs.getString("subject_name"),
            rs.getString("teacher_id") != null ? String.valueOf(rs.getInt("teacher_id")) : null,
            rs.getString("lecturer_name")
        );
    }

    public String getTeacherNameById(int teacherId) throws SQLException, ClassNotFoundException {
        String query = "SELECT name FROM teachers WHERE teacher_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             stmt.setInt(1, teacherId);
             try (ResultSet rs = stmt.executeQuery()) {
                 if (rs.next()) {
                     return rs.getString("name");
                 }
             }
        }
        return "N/A";
    }
}