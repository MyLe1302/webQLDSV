<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Student" %>
<%@ page import="dao.StudentDao" %>
<%@ page import="dao.ClassDAO" %>
<%@ page import="model.ql_class" %>
<%@ page import="model.Account" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thông Tin Sinh Viên</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        .info-container {
            margin-top: 20px;
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            max-width: 400px;
            margin-left: auto;
            margin-right: auto;
        }
        .info-container h3 {
            margin-top: 0;
            text-align: center;
        }
        .info-container table {
            width: 100%;
            border-collapse: collapse;
        }
        .info-container th, .info-container td {
            padding: 10px;
            border-bottom: 1px solid #ddd;
            text-align: left;
        }
        .info-container th {
            background-color: #f2f2f2;
            width: 30%;
        }
        .info-container img {
            display: block;
            margin: 0 auto 20px;
            width: 100px;
            height: 100px;
            border-radius: 50%;
            object-fit: cover;
        }
        .back-button {
        	color: black;
            display: block;
            margin: 20px auto;
            padding: 10px 20px;
            background-color: #ddd;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        
        .back-button:hover {
        	color: white;
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
        String className = "N/A";
        try {
            classId = Integer.parseInt(request.getParameter("class_id"));
            ClassDAO classDao = new ClassDAO();
            ql_class classObj = classDao.getClassById(String.valueOf(classId));
            if (classObj != null) {
                className = classObj.getClassName() != null ? classObj.getClassName() : "N/A";
            }
        } catch (NumberFormatException e) {
            out.println("<p>Lớp học không hợp lệ: " + e.getMessage() + "</p>");
        } catch (Exception e) {
            out.println("<p>Lỗi khi lấy thông tin lớp học: " + e.getMessage() + "</p>");
        }
        Student student = null;
        try {
            StudentDao studentDao = new StudentDao();
            student = studentDao.getStudentById(studentId);
            if (student == null) {
                out.println("<p>Không tìm thấy sinh viên với mã: " + studentId + "</p>");
            }
        } catch (Exception e) {
            out.println("<p>Lỗi khi lấy thông tin sinh viên: " + e.getMessage() + "</p>");
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
        <h2 class="welcome">Thông tin sinh viên</h2>
        <div class="info-container">
            <% if (student != null) { %>
                <img src="images/sv.jpg" alt="Ảnh sinh viên">
                <h3>Thông tin sinh viên</h3>
                <table>
                    <tr><th>Họ tên</th><td><%= student.getName() != null ? student.getName() : "N/A" %></td></tr>
                    <tr><th>Mã sinh viên</th><td><%= student.getStudentId() != null ? student.getStudentId() : "N/A" %></td></tr>
                    <tr><th>Phòng ban</th><td><%= student.getDepartment() != null ? student.getDepartment() : "N/A" %></td></tr>
                    <tr><th>Ngày sinh</th><td><%= student.getBirthDate() != null ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(student.getBirthDate()) : "N/A" %></td></tr>
                    <tr><th>Giới tính</th><td><%= student.getGender() != null ? student.getGender() : "N/A" %></td></tr>
                    <tr><th>Lớp học</th><td><%= className %></td></tr>
                </table>
            <% } else { %>
                <p>Không tìm thấy thông tin sinh viên với mã <%= studentId != null ? studentId : "không xác định" %>.</p>
            <% } %>
        </div>
        <a href="view_students.jsp?class_id=<%= classId %>"><button class="back-button">Quay lại</button></a>
    </main>

    <footer class="footer">
        <p>© 2025 Trường Đại Học Công Nghệ ĐÔNG Á</p>
    </footer>
</body>
</html>