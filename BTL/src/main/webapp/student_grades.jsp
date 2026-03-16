<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Grade" %>
<%@ page import="dao.GradeDao" %>
<%@ page import="model.Account" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bảng Điểm Sinh Viên</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        .grades-container {
            margin-top: 20px;
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            max-width: 800px;
            margin-left: auto;
            margin-right: auto;
        }
        .grades-container h3 {
            margin-top: 0;
            text-align: center;
            color: #333;
        }
        .grades-table {
            margin-top: 20px;
            background-color: white;
            border-radius: 8px;
            overflow-x: auto;
        }
        .grades-table table {
            width: 100%;
            border-collapse: collapse;
            min-width: 600px;
        }
        .grades-table th, .grades-table td {
            padding: 12px;
            text-align: center;
            border-bottom: 1px solid #ddd;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        .grades-table th {
            background-color: #f2f2f2;
            height: 40px;
            line-height: 40px;
        }
        .grades-table tr:hover {
            background-color: #f5f5f5;
        }
        .average-section, .classification-section {
            margin-top: 20px;
        }
        .average-section table, .classification-section table {
            width: 50%;
            margin: 0 auto;
            border-collapse: collapse;
        }
        .average-section th, .average-section td,
        .classification-section th, .classification-section td {
            padding: 10px;
            border-bottom: 1px solid #ddd;
            text-align: left;
        }
        .average-section th, .classification-section th {
            background-color: #f2f2f2;
            width: 50%;
        }
        .back-button {
            display: block;
            margin: 20px auto;
            padding: 10px 20px;
            background-color: #ddd;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            color: #333;
        }
    </style>
</head>
<body>
    <% 
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        String studentId = request.getParameter("student_id");
        int classId = 0;
        try {
            classId = Integer.parseInt(request.getParameter("class_id"));
        } catch (NumberFormatException e) {
            out.println("<p>Lớp học không hợp lệ: " + e.getMessage() + "</p>");
        }
        List<Grade> grades = null;
        double average10 = 0.0;
        double average4 = 0.0;
        String classification = "N/A";
        try {
            GradeDao gradeDao = new GradeDao();
            grades = gradeDao.getGradesByStudentId(studentId, classId);
            if (grades != null && !grades.isEmpty()) {
                double sum = 0.0;
                for (Grade grade : grades) {
                    sum += grade.getTotal();
                }
                average10 = sum / grades.size();
                average4 = (average10 >= 8.5) ? 4.0 : (average10 >= 7.0) ? 3.0 : (average10 >= 5.5) ? 2.0 : 0.0;
                classification = (average10 >= 8.5) ? "Giỏi" : (average10 >= 7.0) ? "Khá" : (average10 >= 5.5) ? "Trung bình" : "Yếu";
            }
        } catch (Exception e) {
            out.println("<p>Lỗi khi lấy bảng điểm: " + e.getMessage() + "</p>");
        }
    %>
    <nav class="navbar">
        <div class="navbar-left">
            <img src="images/Logo.png" alt="Logo" class="logo">
            <h1>TRƯỜNG ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á</h1>
        </div>
        <div class="navbar-right">
            <a href="home.jsp" class="active">Trang Chủ</a>
            <a href="SubjectServlet">Môn Học</a>
            <a href="StudentServlet">Sinh Viên</a>
            <a href="ContactServlet">Liên Hệ</a>
            <a href="AccountServlet">Tài Khoản</a>
            <a href="TeacherServlet">Giảng Viên</a>
            <a href="ClassServlet">Lớp Học</a>
            <a href="ReportServlet">Báo cáo</a>
            <a href="logout" class="logout">Đăng Xuất</a>
        </div>
    </nav>

    <main class="main">
        <h2 class="welcome">Bảng điểm sinh viên</h2>
        <div class="grades-container">
            <% if (grades != null && !grades.isEmpty()) { %>
                <h3>Bảng điểm</h3>
                <div class="grades-table">
                    <table>
                        <thead>
                            <tr>
                                <th>Môn học</th>
                                <th>Chuyên cần</th>
                                <th>Giữa kỳ</th>
                                <th>Cuối kỳ</th>
                                <th>Tổng điểm</th>
                                <th>Điểm chữ</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Grade grade : grades) { %>
                                <tr>
                                    <td><%= grade.getSubjectId() != null ? grade.getSubjectId() : "N/A" %></td>
                                    <td><%= grade.getAttendance() != 0 ? grade.getAttendance() : "N/A" %></td>
                                    <td><%= grade.getMidterm() != 0 ? grade.getMidterm() : "N/A" %></td>
                                    <td><%= grade.getFinalExam() != 0 ? grade.getFinalExam() : "N/A" %></td>
                                    <td><%= grade.getTotal() != 0 ? grade.getTotal() : "N/A" %></td>
                                    <td><%= grade.getLetterGrade() != null ? grade.getLetterGrade() : "N/A" %></td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                <div class="average-section">
                    <h3>Điểm trung bình</h3>
                    <table>
                        <tr><th>Thang điểm 10</th><td><%= String.format("%.1f", average10) %></td></tr>
                        <tr><th>Thang điểm 4</th><td><%= String.format("%.2f", average4) %></td></tr>
                    </table>
                </div>
                <div class="classification-section">
                    <h3>Xếp loại học kỳ</h3>
                    <table>
                        <tr><th>Xếp loại</th><td><%= classification %></td></tr>
                    </table>
                </div>
            <% } else { %>
                <p>Không có dữ liệu bảng điểm cho sinh viên này.</p>
            <% } %>
        </div>
        <a href="view_students.jsp?class_id=<%= classId %>"><button class="back-button">Quay lại</button></a>
    </main>

    <footer class="footer">
        <p>© 2025 Trường Đại Học Công Nghệ ĐÔNG Á</p>
    </footer>
</body>
</html>