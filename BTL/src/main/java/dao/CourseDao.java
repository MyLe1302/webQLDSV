package dao;

import model.Course;
import model.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDao {
    public boolean isCourseExists(int courseId) throws SQLException, ClassNotFoundException {
        String query = "SELECT COUNT(*) FROM courses WHERE course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi kiểm tra khóa học: " + e.getMessage());
            System.out.println("Truy vấn thất bại: " + query);
            throw e;
        }
    }

    // Thêm phương thức lấy tất cả khóa học
    public List<Course> getAllCourses() throws SQLException, ClassNotFoundException {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT course_id, course_name FROM courses";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Course course = new Course(rs.getInt("course_id"), rs.getString("course_name"));
                courses.add(course);
            }
            System.out.println("Số khóa học tìm thấy: " + courses.size());
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy danh sách khóa học: " + e.getMessage());
            System.out.println("Truy vấn thất bại: " + query);
            throw e;
        }
        return courses;
    }

    // Thêm phương thức mới để lấy danh sách ngành học dưới dạng List<String>
    public List<String> getAllCourseNames() throws SQLException {
        List<String> courseNames = new ArrayList<>();
        String query = "SELECT course_name FROM courses";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                courseNames.add(rs.getString("course_name"));
            }
            System.out.println("Số ngành học tìm thấy: " + courseNames.size());
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy danh sách ngành học: " + e.getMessage());
            System.out.println("Truy vấn thất bại: " + query);
            throw e;
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi driver JDBC: " + e.getMessage());
            throw new RuntimeException("Không thể lấy danh sách ngành học do lỗi driver JDBC.", e);
        }
        return courseNames;
    }
}