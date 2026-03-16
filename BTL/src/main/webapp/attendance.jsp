<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Account" %>
<%@ page import="dao.StudentDao" %>
<%@ page import="model.Student" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Điểm danh lớp học</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        .attendance-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            background-color: #fff;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        .attendance-table th, .attendance-table td {
            padding: 12px;
            text-align: center;
            border-bottom: 1px solid #e0e0e0;
        }
        .attendance-table th {
            background-color: #f5f5f5;
            font-weight: 600;
        }
        .action-button {
            padding: 10px 20px;
            margin: 5px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            color: white;
            font-size: 16px;
            transition: background-color 0.3s, transform 0.1s;
        }
        .save-button {
            background-color: #4CAF50;
        }
        .save-button:hover {
            background-color: #45a049;
            transform: translateY(-2px);
        }
        .total-attendance-button {
            background-color: #2196F3;
        }
        .total-attendance-button:hover {
            background-color: #1e88e5;
            transform: translateY(-2px);
        }
        .session-select {
            padding: 8px;
            margin-bottom: 15px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 16px;
        }
        .error-message {
            color: #d32f2f;
            text-align: center;
            margin: 10px 0;
        }
        .success-message {
            color: #388e3c;
            text-align: center;
            margin: 10px 0;
        }
        .main h2 {
            color: #2196F3; /* Màu xanh tương tự trang home */
            text-align: center;
            margin-bottom: 20px;
        }
        .button-container {
            text-align: center;
            margin-top: 15px;
        }
        select[name^="status_"] {
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 4px;
    font-size: 14px;
    background-color: #fff;
    cursor: pointer;
    transition: border-color 0.3s, box-shadow 0.3s;
    width: 150px;
}

select[name^="status_"]:hover {
    border-color: #2196F3;
}

select[name^="status_"]:focus {
    outline: none;
    border-color: #2196F3;
    box-shadow: 0 0 5px rgba(33, 150, 243, 0.3);
}

/* CSS cho ghi chú (input) */
input[name^="note_"] {
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 4px;
    font-size: 14px;
    width: 200px;
    transition: border-color 0.3s, box-shadow 0.3s;
}

input[name^="note_"]:hover {
    border-color: #2196F3;
}

input[name^="note_"]:focus {
    outline: none;
    border-color: #2196F3;
    box-shadow: 0 0 5px rgba(33, 150, 243, 0.3);
}
.message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .success { background-color: #d4edda; color: #155724; }
        .error { background-color: #f8d7da; color: #721c24; }
    </style>
    <script>
        function loadAttendance() {
            var sessionNumber = document.getElementById("session_number").value;
            var classId = document.getElementById("class_id").value;
            window.location.href = "attendance.jsp?class_id=" + classId + "&session_number=" + (sessionNumber || "0");
        }
    </script>
</head>
<body>
    <%
        Account account = (Account) session.getAttribute("account");
        if (account == null || (!account.getRole().equals("teacher") && !account.getRole().equals("admin"))) {
            response.sendRedirect("login.jsp");
            return;
        }
        String classId = request.getParameter("class_id");
        if (classId == null || classId.trim().isEmpty()) {
            response.sendRedirect("home.jsp");
            return;
        }
        String sessionNumberParam = request.getParameter("session_number");
        int sessionNumber = sessionNumberParam != null && !sessionNumberParam.equals("0") ? Integer.parseInt(sessionNumberParam) : 0;
        StudentDao studentDao = new StudentDao();
        List<Student> students = null;
        boolean showTotalAbsences = request.getParameter("showTotal") != null;
        String error = request.getParameter("error");
        String success = request.getParameter("success");
        Map<String, Map<String, String>> attendanceData = null;
        try {
            students = studentDao.getStudentsByClassId(Integer.parseInt(classId));
            if (sessionNumber > 0 && !showTotalAbsences) {
                attendanceData = studentDao.getAttendanceForSession(Integer.parseInt(classId), sessionNumber);
            }
        } catch (Exception e) {
            error = "Lỗi khi lấy danh sách sinh viên: " + e.getMessage();
        }
    %>
    <nav class="navbar">
        <div class="navbar-left">
            <img src="images/Logo.png" alt="Logo" class="logo">
            <h1>TRƯỜNG ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á</h1>
        </div>
        <div class="navbar-right">
            <a href="home.jsp">Trang Chủ</a>
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
        <h2>Điểm danh lớp học</h2>
        <% if (error != null) { %>
            <p class="mesage error"><%= error %></p>
        <% } %>
        <% if (success != null) { %>
            <p class="message success"><%= success %></p>
        <% } %>

        <!-- Form cho điểm danh -->
        <% if (!showTotalAbsences) { %>
            <form action="AttendanceServlet" method="post">
                <input type="hidden" name="class_id" id="class_id" value="<%= classId %>">
                <label for="session_number">Chọn buổi học:</label>
                <select name="session_number" id="session_number" class="session-select" onchange="loadAttendance()">
                    <option value="0">-- Chọn buổi --</option>
                    <% for (int i = 1; i <= 15; i++) { %>
                        <option value="<%= i %>" <%= sessionNumber == i ? "selected" : "" %>>Buổi <%= i %></option>
                    <% } %>
                </select>
                <table class="attendance-table">
                    <thead>
                        <tr>
                            <th>Mã sinh viên</th>
                            <th>Tên sinh viên</th>
                            <th>Trạng thái</th>
                            <th>Ghi chú</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            if (students != null && !students.isEmpty()) {
                                for (Student student : students) {
                                    Map<String, String> record = attendanceData != null ? attendanceData.get(student.getStudentId()) : null;
                                    String status = record != null ? record.get("status") : "present";
                                    String note = record != null ? record.get("note") : "";
                        %>
                        <tr>
                            <td><%= student.getStudentId() %></td>
                            <td><%= student.getName() %></td>
                            <td>
                                <select name="status_<%= student.getStudentId() %>" required>
                                    <option value="present" <%= "present".equals(status) ? "selected" : "" %>>Có mặt</option>
                                    <option value="absent_without_permission" <%= "absent_without_permission".equals(status) ? "selected" : "" %>>Vắng không phép</option>
                                    <option value="absent_with_permission" <%= "absent_with_permission".equals(status) ? "selected" : "" %>>Vắng có phép</option>
                                </select>
                            </td>
                            <td>
                                <input type="text" name="note_<%= student.getStudentId() %>" value="<%= note %>" placeholder="Ghi chú (tùy chọn)">
                            </td>
                        </tr>
                        <%
                                }
                            } else {
                                out.println("<tr><td colspan='4'>Không có sinh viên nào trong lớp này.</td></tr>");
                            }
                        %>
                    </tbody>
                </table>
                <div class="button-container">
                    <button type="submit" name="action" value="save" class="action-button save-button">Lưu điểm danh</button>
                </div>
            </form>
        <% } %>

        <!-- Form cho lấy tổng số buổi nghỉ -->
        <form action="AttendanceServlet" method="post">
            <input type="hidden" name="class_id" value="<%= classId %>">
            <% if (showTotalAbsences) { %>
                <table class="attendance-table">
                    <thead>
                        <tr>
                            <th>Mã sinh viên</th>
                            <th>Tên sinh viên</th>
                            <th>Tổng số buổi nghỉ</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            if (students != null && !students.isEmpty()) {
                                for (Student student : students) {
                        %>
                        <tr>
                            <td><%= student.getStudentId() %></td>
                            <td><%= student.getName() %></td>
                            <td>
                                <%
                                    try {
                                        int totalAbsences = studentDao.getTotalAbsences(student.getStudentId(), Integer.parseInt(classId));
                                        out.print(totalAbsences);
                                    } catch (Exception e) {
                                        out.print("N/A");
                                    }
                                %>
                            </td>
                        </tr>
                        <%
                                }
                            } else {
                                out.println("<tr><td colspan='3'>Không có sinh viên nào trong lớp này.</td></tr>");
                            }
                        %>
                    </tbody>
                </table>
                <div class="button-container">
                    <form action="attendance.jsp" method="get">
                        <input type="hidden" name="class_id" value="<%= classId %>">
                        <button type="submit" class="action-button save-button">Quay lại điểm danh</button>
                    </form>
                </div>
            <% } %>
            <% if (!showTotalAbsences) { %>
                <div class="button-container">
                    <button type="submit" name="action" value="showTotal" class="action-button total-attendance-button">Xem điểm danh</button>
                </div>
            <% } %>
        </form>
    </main>

    <footer class="footer">
        <p>© 2025 Trường Đại Học Công Nghệ ĐÔNG Á</p>
    </footer>
</body>
</html>