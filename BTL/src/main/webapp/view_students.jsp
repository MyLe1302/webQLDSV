<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Student" %>
<%@ page import="model.Account" %>
<%@ page import="dao.StudentDao" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Xem Danh Sách Sinh Viên</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        .students-table {
            margin-top: 20px;
            margin-bottom: 20px;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            overflow-x: auto;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            min-width: 800px;
        }
        th, td {
            padding: 12px;
            text-align: center;
            border-bottom: 1px solid #ddd;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        th {
            background-color: #f2f2f2;
        }
        th:nth-child(1), td:nth-child(1) { width: 10%; }
        th:nth-child(2), td:nth-child(2) { width: 20%; }
        th:nth-child(3), td:nth-child(3) { width: 18%; }
        th:nth-child(4), td:nth-child(4) { width: 15%; }
        th:nth-child(5), td:nth-child(5) { width: 20%; }
        tr:hover {
            background-color: #f5f5f5;
        }
        .back-button-container {
            margin-top: 20px;
            margin-bottom: 20px;
            display: flex;
        }
        .search-container {
            margin-bottom: 15px;
        }
        .search-container input {
            padding: 8px;
            width: 200px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 0.9rem;
        }
        .action-button {
            padding: 5px 10px;
            margin: 0 5px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 0.9rem;
            display: inline-block;
            text-decoration: none;
            color: white;
        }
        .info-button {
            background-color: #4CAF50;
            width: auto;
        }
        .grades-button {
            background-color: #2196F3;
            width: auto;
        }
        .action-button-container {
            display: flex;
            gap: 5px;
            justify-content: center;
            align-items: center;
        }
    </style>
    <script>
        document.addEventListener("DOMContentLoaded", function() {
            const rows = document.querySelectorAll("table tbody tr");

            document.querySelector(".search-input").addEventListener("input", function() {
                const searchValue = this.value.toLowerCase();
                rows.forEach(row => {
                    const studentId = row.cells[0].textContent.toLowerCase();
                    const name = row.cells[1].textContent.toLowerCase();
                    if (studentId.includes(searchValue) || name.includes(searchValue)) {
                        row.style.display = "";
                    } else {
                        row.style.display = "none";
                    }
                });
            });
        });
    </script>
</head>
<body>
    <% 
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        String classIdStr = request.getParameter("class_id");
        int classId = 0;
        String className = "N/A"; // Giá trị mặc định
        try {
            if (classIdStr != null && !classIdStr.trim().isEmpty()) {
                classId = Integer.parseInt(classIdStr);
                StudentDao studentDao = new StudentDao();
                className = studentDao.getClassNameById(classId); // Giả định phương thức lấy tên lớp
                if (className == null || className.trim().isEmpty()) {
                    className = "Lớp không xác định";
                }
            }
        } catch (NumberFormatException e) {
            out.println("<p>Lớp học không hợp lệ. Sử dụng giá trị mặc định: 0</p>");
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
        <h2 class="welcome">Danh sách sinh viên - <%= className %></h2>
        
        <div class="search-container">
            <input type="text" class="search-input" placeholder="Tìm theo mã hoặc tên sinh viên...">
        </div>
        <div class="students-table">
            <table>
                <thead>
                    <tr>
                        <th>Mã sinh viên</th>
                        <th>Tên sinh viên</th>
                        <th>Phòng ban</th>
                        <th>Ngày sinh</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        try {
                            StudentDao studentDao = new StudentDao();
                            List<Student> students = studentDao.getStudentsByClassId(classId);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            if (students != null && !students.isEmpty()) {
                                for (Student student : students) {
                    %>
                        <tr>
                            <td><%= student.getStudentId() != null ? student.getStudentId() : "N/A" %></td>
                            <td><%= student.getName() != null ? student.getName() : "N/A" %></td>
                            <td><%= student.getDepartment() != null ? student.getDepartment() : "N/A" %></td>
                            <td><%= student.getBirthDate() != null ? dateFormat.format(student.getBirthDate()) : "N/A" %></td>
                            <td>
                                <div class="action-button-container">
                                    <a href="student_info.jsp?student_id=<%= student.getStudentId() %>&class_id=<%= classId %>">
                                        <button class="action-button info-button">Thông tin</button>
                                    </a>
                                    <a href="student_grades.jsp?student_id=<%= student.getStudentId() %>&class_id=<%= classId %>">
                                        <button class="action-button grades-button">Bảng điểm</button>
                                    </a>
                                </div>
                            </td>
                        </tr>
                    <% 
                                }
                            } else {
                                out.println("<tr><td colspan='5'>Không có sinh viên nào trong lớp này.</td></tr>");
                            }
                        } catch (Exception e) {
                            out.println("<tr><td colspan='5'>Lỗi khi lấy dữ liệu: " + e.getMessage() + "</td></tr>");
                            e.printStackTrace();
                        }
                    %>
                </tbody>
            </table>
        </div>

        <a href="home.jsp"><button class="back-button">Quay lại</button></a>
    </main>

    <footer class="footer">
        <p>© 2025 Trường Đại Học Công Nghệ ĐÔNG Á</p>
    </footer>
</body>
</html>