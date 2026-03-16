package dao;

import model.Account;
import model.DatabaseConnection;
import model.Report;
import model.Subject;
import model.ql_class;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportDao {

    public List<Report> getReports(Account account, String classId, String subjectId) throws SQLException, ClassNotFoundException {
        List<Report> reports = new ArrayList<>();
        StringBuilder query = new StringBuilder(
            "SELECT c.class_id, c.class_name, s.subject_id, s.subject_name, " +
            "COUNT(DISTINCT g.student_id) AS total_students, " +
            "AVG(g.total) AS average_score, " +
            "MAX(g.total) AS highest_score, " +
            "MIN(g.total) AS lowest_score, " +
            "SUM(CASE WHEN g.letter_grade = 'A' COLLATE utf8mb4_unicode_ci THEN 1 ELSE 0 END) AS count_grade_a, " +
            "SUM(CASE WHEN g.letter_grade = 'B' COLLATE utf8mb4_unicode_ci THEN 1 ELSE 0 END) AS count_grade_b, " +
            "SUM(CASE WHEN g.letter_grade = 'C' COLLATE utf8mb4_unicode_ci THEN 1 ELSE 0 END) AS count_grade_c, " +
            "SUM(CASE WHEN g.letter_grade = 'D' COLLATE utf8mb4_unicode_ci THEN 1 ELSE 0 END) AS count_grade_d " +
            "FROM classes c " +
            "LEFT JOIN student_classes sc ON c.class_id = sc.class_id COLLATE utf8mb4_unicode_ci " +
            "LEFT JOIN students st ON sc.student_id = st.student_id COLLATE utf8mb4_unicode_ci " +
            "LEFT JOIN grades g ON st.student_id = g.student_id COLLATE utf8mb4_unicode_ci " +
            "LEFT JOIN subjects s ON g.subject_id = s.subject_id COLLATE utf8mb4_unicode_ci "
        );

        if (account.getRole().equals("teacher")) {
            query.append("JOIN teachers t ON c.teacher_id = t.teacher_id COLLATE utf8mb4_unicode_ci WHERE t.account_id = ? ");
        } else if (account.getRole().equals("student")) {
            query.append("WHERE st.account_id = ? ");
        } else if (!account.getRole().equals("admin")) {
            System.out.println("Vai trò không hợp lệ: " + account.getRole());
            return reports;
        }

        if (classId != null && !classId.isEmpty()) {
            query.append(account.getRole().equals("admin") ? "WHERE c.class_id = ? COLLATE utf8mb4_unicode_ci " : "AND c.class_id = ? COLLATE utf8mb4_unicode_ci ");
        }
        if (subjectId != null && !subjectId.isEmpty()) {
            query.append(classId != null && !classId.isEmpty() ? "AND s.subject_id = ? COLLATE utf8mb4_unicode_ci " : "WHERE s.subject_id = ? COLLATE utf8mb4_unicode_ci ");
        }

        query.append("GROUP BY c.class_id, c.class_name, s.subject_id, s.subject_name");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            int paramIndex = 1;
            if (!account.getRole().equals("admin")) {
                stmt.setInt(paramIndex++, account.getAccountId());
            }
            if (classId != null && !classId.isEmpty()) {
                stmt.setString(paramIndex++, classId);
            }
            if (subjectId != null && !subjectId.isEmpty()) {
                stmt.setString(paramIndex++, subjectId);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Report report = new Report(
                    rs.getString("class_id"),
                    rs.getString("subject_id"),
                    rs.getString("class_name"),
                    rs.getString("subject_name"),
                    rs.getInt("total_students"),
                    rs.getFloat("average_score"),
                    rs.getFloat("highest_score"),
                    rs.getFloat("lowest_score"),
                    rs.getInt("count_grade_a"),
                    rs.getInt("count_grade_b"),
                    rs.getInt("count_grade_c"),
                    rs.getInt("count_grade_d")
                );
                reports.add(report);
            }
            System.out.println("Số báo cáo tìm thấy: " + reports.size());
        } catch (SQLException e) {
            System.out.println("Lỗi SQL: " + e.getMessage());
            throw e;
        }
        return reports;
    }

    public List<ql_class> getAllClasses(Account account) throws SQLException, ClassNotFoundException {
        ClassDAO classDao = new ClassDAO();
        return classDao.getAllClasses(account);
    }

    public List<Subject> getAllSubjects() throws SQLException, ClassNotFoundException {
        SubjectDao subjectDao = new SubjectDao();
        return subjectDao.getAllSubjects();
    }
}