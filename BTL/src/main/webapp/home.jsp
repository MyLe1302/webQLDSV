<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Account" %>
<%@ page import="dao.ClassDAO" %>
<%@ page import="dao.AccountDao" %>
<%@ page import="model.ql_class" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trang Chủ</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
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
        .view-students {
            background-color: #4CAF50;
        }
        .manage-grades {
            background-color: #2196F3;
        }
        .attendance-button {
            background-color: #FF9800;
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            padding: 8px;
            text-align: center !important;
            border-bottom: 1px solid #ddd;
        }
        th:nth-child(1), td:nth-child(1) { /* Mã lớp */
            width: 10%;
            text-align: center !important;
        }
        th:nth-child(2), td:nth-child(2) { /* Tên lớp */
            width: 20%;
            text-align: center !important;
        }
        th:nth-child(3), td:nth-child(3) { /* Tên môn học */
            width: 23%;
            text-align: center !important;
        }
        th:nth-child(4), td:nth-child(4) { /* Tên giảng viên */
            width: 17%;
            text-align: center !important;
        }
        th:nth-child(5), td:nth-child(5) { /* Hành động */
            width: 30%;
            text-align: center !important;
        }
        .action-button-container {
            display: flex;
            gap: 5px;
            justify-content: center;
            align-items: center;
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
        <%
            // Bổ sung: Lấy tên người dùng từ AccountDao
            AccountDao accountDao = new AccountDao();
            String displayName = account.getName();
            try {
                if (displayName == null || displayName.isEmpty()) {
                    displayName = accountDao.getNameByAccountId(account.getAccountId(), account.getRole());
                }
            } catch (Exception e) {
                displayName = "Người dùng";
            }
        %>
        <h2 class="welcome">Chào mừng, <%= displayName %>!</h2>
        <p class="email">Email: <%= account.getEmail() != null ? account.getEmail() : "N/A" %></p>
        <h3 class="classes-title">Lớp học của bạn:</h3>
        <div class="classes-table">
            <table>
                <thead>
                    <tr>
                        <th>Mã lớp</th>
                        <th>Tên lớp</th>
                        <th>Tên môn học</th>
                        <th>Tên giảng viên</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        try {
                            ClassDAO classDao = new ClassDAO();
                            List<ql_class> classes = classDao.getAllClasses(account);
                            if (classes != null && !classes.isEmpty()) {
                                for (ql_class classObj : classes) {
                    %>
                        <tr>
                            <td><%= classObj.getClassId() != null ? classObj.getClassId() : "N/A" %></td>
                            <td><%= classObj.getClassName() != null ? classObj.getClassName() : "N/A" %></td>
                            <td><%= classObj.getSubjectName() != null ? classObj.getSubjectName() : "N/A" %></td>
                            <%
                                // Bổ sung: Lấy tên giảng viên từ ClassDAO
                                String lecturerName = classObj.getLecturer();
                                try {
                                    if (lecturerName == null || lecturerName.isEmpty()) {
                                        if (classObj.getTeacherId() != null && !classObj.getTeacherId().isEmpty()) {
                                            try {
                                                lecturerName = classDao.getTeacherNameById(Integer.parseInt(classObj.getTeacherId()));
                                            } catch (NumberFormatException e) {
                                                lecturerName = "N/A";
                                            }
                                        } else {
                                            lecturerName = "N/A";
                                        }
                                    }
                                } catch (Exception e) {
                                    lecturerName = "N/A";
                                }
                            %>
                            <td><%= lecturerName %></td>
                            <td>
                                <div class="action-button-container">
                                    <a href="view_students.jsp?class_id=<%= classObj.getClassId() %>">
                                        <button class="action-button view-students">Xem sinh viên</button>
                                    </a>
                                    <% if (account.getRole().equals("teacher") || account.getRole().equals("admin")) { %>
                                        <a href="GradeServlet?class_id=<%= classObj.getClassId() %>">
                                            <button class="action-button manage-grades">Quản lý điểm</button>
                                        </a>
                                        <a href="attendance.jsp?class_id=<%= classObj.getClassId() %>">
                                            <button class="action-button attendance-button">Điểm danh</button>
                                        </a>
                                    <% } %>
                                </div>
                            </td>
                        </tr>
                    <% 
                                }
                            } else {
                                out.println("<tr><td colspan='5'>Không có lớp học nào để hiển thị.</td></tr>");
                            }
                        } catch (Exception e) {
                            out.println("<tr><td colspan='5'>Lỗi khi lấy dữ liệu: " + e.getMessage() + "</td></tr>");
                            e.printStackTrace();
                        }
                    %>
                </tbody>
            </table>
        </div>
    </main>

    <footer class="footer">
        <p>© 2025 Trường Đại Học Công Nghệ ĐÔNG Á</p>
    </footer>
</body>
</html>