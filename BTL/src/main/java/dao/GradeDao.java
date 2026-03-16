package dao;

import model.Grade;
import model.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GradeDao {
    public List<Grade> getGradesByClassId(int classId) throws SQLException, ClassNotFoundException {
        List<Grade> grades = new ArrayList<>();
        String query = "SELECT g.*, s.subject_name " +
                      "FROM grades g " +
                      "JOIN subjects s ON g.subject_id = s.subject_id " +
                      "JOIN student_classes sc ON g.student_id = sc.student_id " +
                      "WHERE sc.class_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, classId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Grade grade = new Grade(
                        rs.getInt("grade_id"),
                        rs.getString("student_id"),
                        rs.getString("subject_id"),
                        rs.getFloat("score"),
                        rs.getFloat("attendance"),
                        rs.getFloat("midterm"),
                        rs.getFloat("final_exam"),
                        rs.getFloat("total"),
                        rs.getString("letter_grade"),
                        rs.getString("note")
                    );
                    grade.setSubjectName(rs.getString("subject_name"));
                    // Tính total nếu chưa có (giả định: 10% attendance, 30% midterm, 60% final_exam)
                    if (grade.getTotal() <= 0 && grade.getAttendance() >= 0 && grade.getMidterm() >= 0 && grade.getFinalExam() >= 0) {
                        grade.setTotal((float) (grade.getAttendance() * 0.1 + grade.getMidterm() * 0.3 + grade.getFinalExam() * 0.6));
                    }
                    grades.add(grade);
                }
                System.out.println("Số điểm tìm thấy trong lớp " + classId + ": " + grades.size());
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy điểm theo lớp: " + e.getMessage());
            throw e;
        }
        return grades;
    }

    public List<Grade> getGradesByStudentId(String studentId, int classId) throws SQLException, ClassNotFoundException {
        List<Grade> grades = new ArrayList<>();
        String query = "SELECT g.*, s.subject_name " +
                      "FROM grades g " +
                      "JOIN subjects s ON g.subject_id = s.subject_id " +
                      "JOIN student_classes sc ON g.student_id = sc.student_id " +
                      "WHERE g.student_id = ? AND sc.class_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studentId);
            stmt.setInt(2, classId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Grade grade = new Grade(
                        rs.getInt("grade_id"),
                        rs.getString("student_id"),
                        rs.getString("subject_id"),
                        rs.getFloat("score"),
                        rs.getFloat("attendance"),
                        rs.getFloat("midterm"),
                        rs.getFloat("final_exam"),
                        rs.getFloat("total"),
                        rs.getString("letter_grade"),
                        rs.getString("note")
                    );
                    grade.setSubjectName(rs.getString("subject_name"));
                    // Tính total nếu chưa có
                    if (grade.getTotal() <= 0 && grade.getAttendance() >= 0 && grade.getMidterm() >= 0 && grade.getFinalExam() >= 0) {
                        grade.setTotal((float) (grade.getAttendance() * 0.1 + grade.getMidterm() * 0.3 + grade.getFinalExam() * 0.6));
                    }
                    grades.add(grade);
                }
                System.out.println("Số điểm tìm thấy cho sinh viên " + studentId + " trong lớp " + classId + ": " + grades.size());
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy điểm theo sinh viên: " + e.getMessage());
            throw e;
        }
        return grades;
    }

    public void addGrade(Grade grade) throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO grades (student_id, subject_id, score, attendance, midterm, final_exam, total, letter_grade, note) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, grade.getStudentId());
            stmt.setString(2, grade.getSubjectId());
            stmt.setFloat(3, grade.getScore());
            stmt.setFloat(4, grade.getAttendance());
            stmt.setFloat(5, grade.getMidterm());
            stmt.setFloat(6, grade.getFinalExam());
            stmt.setFloat(7, grade.getTotal());
            stmt.setString(8, grade.getLetterGrade());
            stmt.setString(9, grade.getNote());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Không thể thêm điểm số");
            }
        }
    }

    public void updateGrade(Grade grade) throws SQLException, ClassNotFoundException {
        String query = "UPDATE grades SET student_id = ?, subject_id = ?, score = ?, attendance = ?, midterm = ?, " +
                      "final_exam = ?, total = ?, letter_grade = ?, note = ? WHERE grade_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, grade.getStudentId());
            stmt.setString(2, grade.getSubjectId());
            stmt.setFloat(3, grade.getScore());
            stmt.setFloat(4, grade.getAttendance());
            stmt.setFloat(5, grade.getMidterm());
            stmt.setFloat(6, grade.getFinalExam());
            stmt.setFloat(7, grade.getTotal());
            stmt.setString(8, grade.getLetterGrade());
            stmt.setString(9, grade.getNote());
            stmt.setInt(10, grade.getGradeId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Không tìm thấy điểm số với mã " + grade.getGradeId());
            }
        }
    }

    public void deleteGrade(int gradeId) throws SQLException, ClassNotFoundException {
        String query = "DELETE FROM grades WHERE grade_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, gradeId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Không tìm thấy điểm số với mã " + gradeId);
            }
        }
    }

    public void updateAttendanceScore(String studentId, String subjectId, float score) 
            throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO grades (student_id, subject_id, attendance) " +
                      "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE attendance = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, subjectId);
            pstmt.setFloat(3, score);
            pstmt.setFloat(4, score);
            pstmt.executeUpdate();
        }
    }
}