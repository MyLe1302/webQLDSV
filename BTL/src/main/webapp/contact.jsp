<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Contact" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Liên Hệ</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        
        .container {
            width: 90%;
            margin: 20px auto;
            padding: 20px;
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        .form-group input, .form-group textarea {
            width: 100%;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
        }
        .form-group button {
            background-color: #28a745;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .form-group button:hover {
            background-color: #218838;
        }
        .contact-list {
            margin-top: 20px;
        }
        .contact-list table {
            width: 100%;
            border-collapse: collapse;
        }
        .contact-list th, .contact-list td {
            padding: 8px;
            border: 1px solid #ddd;
            text-align: left;
        }
        .contact-list th {
            background-color: #f2f2f2;
        }
        .contact-list tr:nth-child(even) {
            background-color: #f9f9f9;
        }
        .error {
            color: red;
            font-weight: bold;
            margin-top: 10px;
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="navbar-left">
            <img src="images/Logo.png" alt="Logo" class="logo">
            <h1>TRƯỜNG ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á</h1>
        </div>
        <div class="navbar-right">
            <a href="home.jsp">Trang Chủ</a>
            <a href="SubjectServlet">Môn Học</a>
            <a href="StudentServlet">Sinh Viên</a>
            <a href="ContactServlet" class="active">Liên Hệ</a>
            <a href="AccountServlet">Tài Khoản</a>
            <a href="TeacherServlet">Giảng Viên</a>
            <a href="ClassServlet">Lớp Học</a>
            <a href="ReportServlet">Báo cáo</a>
            <a href="logout" class="logout">Đăng Xuất</a>
        </div>
    </nav>

    <div class="container">
        <h2>Liên Hệ</h2>
        <% if (request.getAttribute("errorMessage") != null) { %>
            <p class="error"><%= request.getAttribute("errorMessage") %></p>
        <% } %>
        <% if (request.getAttribute("successMessage") != null) { %>
            <p style="color: green; font-weight: bold; margin-top: 10px;"><%= request.getAttribute("successMessage") %></p>
        <% } %>

        <form action="ContactServlet" method="post">
            <div class="form-group">
                <label for="name">Họ và tên</label>
                <input type="text" id="name" name="name" required>
            </div>
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" required>
            </div>
            <div class="form-group">
                <label for="message">Tin nhắn</label>
                <textarea id="message" name="message" rows="4" required></textarea>
            </div>
            <div class="form-group">
                <button type="submit">Gửi liên hệ</button>
            </div>
        </form>

        <div class="contact-list">
            <h3>Danh sách liên hệ</h3>
            <table>
                <thead>
                    <tr>
                        <th>Họ và tên</th>
                        <th>Email</th>
                        <th>Tin nhắn</th>
                        <th>Ngày gửi</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        List<Contact> contacts = (List<Contact>) request.getAttribute("contacts");
                        if (contacts == null) {
                            contacts = new ArrayList<>();
                        }
                        if (!contacts.isEmpty()) {
                            for (Contact contact : contacts) {
                    %>
                        <tr>
                            <td><%= contact.getName() != null ? contact.getName() : "" %></td>
                            <td><%= contact.getEmail() != null ? contact.getEmail() : "" %></td>
                            <td><%= contact.getMessage() != null ? contact.getMessage() : "" %></td>
                            <td><%= contact.getCreatedAt() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(contact.getCreatedAt()) : "" %></td>
                        </tr>
                    <% 
                            }
                        } else {
                    %>
                        <tr><td colspan="4">Không có dữ liệu liên hệ.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>

    <footer class="footer">
        <p>© 2025 Trường Đại Học Công Nghệ ĐÔNG Á</p>
    </footer>
</body>
</html>