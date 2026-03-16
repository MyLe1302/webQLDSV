<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Account" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Tài Khoản</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        .form-container {
            padding: 15px;
            display: flex;
            flex-wrap: wrap;
            gap: 15px;
            background-color: white;
            border-radius: 0.5rem;
            box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.1);
            margin-bottom: 20px;
        }

        .form-container > div {
            flex: 1 1 200px;
        }

        .form-container label {
            display: block;
            font-size: 0.9rem;
            color: #666;
            margin-bottom: 5px;
        }

        .form-container input, .form-container select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 0.9rem;
            box-sizing: border-box;
        }

        .form-container input[type="checkbox"] {
            width: auto;
            margin-top: 10px;
        }

        .action-buttons {
            flex: 1 1 100%;
            display: flex;
            gap: 10px;
            justify-content: flex-start;
            margin-top: 10px;
        }

        .action-buttons button {
            padding: 8px 12px;
            font-size: 0.8rem;
            border: none;
            border-radius: 0.25rem;
            cursor: pointer;
            transition: background-color 0.3s ease;
            height: 30px;
            margin-top: 13px;
        }

        .action-buttons .add-btn {
            background-color: #28a745;
            color: white;
        }

        .action-buttons .add-btn:hover {
            background-color: #218838;
        }

        .action-buttons .edit-btn, .action-buttons .delete-btn {
            background-color: #ccc;
            color: #333;
            pointer-events: none;
        }

        .action-buttons .edit-btn.active, .action-buttons .delete-btn.active {
            background-color: #007BFF;
            color: white;
            pointer-events: auto;
        }

        .action-buttons .edit-btn.active:hover {
            background-color: #0056b3;
        }

        .action-buttons .delete-btn.active {
            background-color: #dc3545;
        }

        .action-buttons .delete-btn.active:hover {
            background-color: #c82333;
        }

        .accounts-table {
            margin: 20px 0;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            overflow-x: auto;
        }

        table {
            min-width: 800px;
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 8px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }

        th {
            background-color: #f2f2f2;
        }

        tr.selected {
            background-color: #e6f3ff;
            font-weight: bold;
        }

        tr:hover {
            background-color: #f5f5f5;
        }

        .back-button-container {
            margin-top: 20px;
            margin-bottom: 20px;
            
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
        
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .success { background-color: #d4edda; color: #155724; }
        .error { background-color: #f8d7da; color: #721c24; }
        
    </style>
    <script>
        document.addEventListener("DOMContentLoaded", function() {
            const rows = document.querySelectorAll("table tbody tr");
            let selectedRow = null;

            rows.forEach(row => {
                row.addEventListener("click", function(e) {
                    if (selectedRow) selectedRow.classList.remove("selected");
                    this.classList.add("selected");
                    selectedRow = this;

                    const accountId = this.getAttribute("data-account-id");
                    const username = this.cells[0].textContent;
                    const email = this.cells[1].textContent;
                    const role = this.cells[2].textContent;
                    const approved = this.cells[3].textContent === "✓";
                    const password = this.getAttribute("data-password") || "";

                    const form = document.querySelector(".form-container");
                    form.querySelector('input[name="action"]').value = "update";
                    form.querySelector('input[name="account_id"]').value = accountId;
                    form.querySelector('input[name="username"]').value = username;
                    form.querySelector('input[name="password"]').value = password;
                    form.querySelector('input[name="email"]').value = email;
                    form.querySelector('select[name="role"]').value = role.toLowerCase();
                    form.querySelector('input[name="approved"]').checked = approved;

                    const editBtn = document.querySelector(".edit-btn");
                    const deleteBtn = document.querySelector(".delete-btn");
                    editBtn.classList.add("active");
                    deleteBtn.classList.add("active");
                });
            });

            document.querySelector(".add-btn").addEventListener("click", function() {
                const form = document.querySelector(".form-container");
                const username = form.querySelector('input[name="username"]').value.trim();
                const password = form.querySelector('input[name="password"]').value.trim();
                const email = form.querySelector('input[name="email"]').value.trim();
                const role = form.querySelector('select[name="role"]').value;

                // Validation phía client
                if (!username || !password || !email || !role) {
                    alert("Vui lòng điền đầy đủ thông tin bắt buộc!");
                    return;
                }

                // Log dữ liệu để debug
                console.log("Form data before submit:", {
                    action: "add",
                    username: username,
                    password: password,
                    email: email,
                    role: role,
                    approved: form.querySelector('input[name="approved"]').checked
                });

                // Đặt action và submit form mà không xóa dữ liệu
                form.querySelector('input[name="action"]').value = "add";
                form.querySelector('input[name="account_id"]').value = "";
                form.submit();

                // Xóa dữ liệu sau khi submit
                form.querySelector('input[name="username"]').value = "";
                form.querySelector('input[name="password"]').value = "";
                form.querySelector('input[name="email"]').value = "";
                form.querySelector('select[name="role"]').value = "";
                form.querySelector('input[name="approved"]').checked = false;
                const editBtn = document.querySelector(".edit-btn");
                const deleteBtn = document.querySelector(".delete-btn");
                editBtn.classList.remove("active");
                deleteBtn.classList.remove("active");
                if (selectedRow) selectedRow.classList.remove("selected");
                selectedRow = null;
            });

            document.querySelector(".edit-btn").addEventListener("click", function() {
                if (this.classList.contains("active") && selectedRow) {
                    document.querySelector(".form-container").submit();
                }
            });

            document.querySelector(".delete-btn").addEventListener("click", function() {
                if (this.classList.contains("active") && selectedRow) {
                    if (confirm("Bạn có chắc muốn xóa tài khoản này?")) {
                        const form = document.querySelector(".form-container");
                        form.querySelector('input[name="action"]').value = "delete";
                        form.submit();
                    }
                } else {
                    alert("Vui lòng chọn một dòng để xóa!");
                }
            });

            // Thêm chức năng tìm kiếm
            document.querySelector(".search-input").addEventListener("input", function() {
                const searchValue = this.value.toLowerCase();
                rows.forEach(row => {
                    const username = row.cells[0].textContent.toLowerCase();
                    const email = row.cells[1].textContent.toLowerCase();
                    if (username.includes(searchValue) || email.includes(searchValue)) {
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
        // Kiểm tra quyền truy cập
        Account currentUser = (Account) session.getAttribute("account");
        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        if (!"admin".equals(currentUser.getRole())) {
    %>
        <div class="error-message">Bạn không có quyền truy cập trang này!</div>
        <a href="home.jsp" class="back-button">Quay lại Trang Chủ</a>
    <%
            return;
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
            <a href="AccountServlet" class="active">Tài Khoản</a>
            <a href="TeacherServlet">Giảng Viên</a>
            <a href="ClassServlet">Lớp Học</a>
            <a href="ReportServlet">Báo cáo</a>
            <a href="logout" class="logout">Đăng Xuất</a>
        </div>
    </nav>

    <main class="main">
        <h2 class="welcome">Quản lý tài khoản</h2>
        <% if (request.getAttribute("errorMessage") != null) { %>
            <p class="message error"><%= request.getAttribute("errorMessage") %></p>
        <% } %>
        <% if (request.getAttribute("successMessage") != null) { %>
            <p class="message success"><%= request.getAttribute("successMessage") %></p>
        <% } %>

        <form action="AccountServlet" method="post" class="form-container">
            <input type="hidden" name="action" value="add">
            <input type="hidden" name="account_id">
            <div>
                <label>Tên người dùng:</label>
                <input type="text" name="username" required>
            </div>
            <div>
                <label>Mật khẩu:</label>
                <input type="password" name="password" required>
            </div>
            <div>
                <label>Email:</label>
                <input type="email" name="email" required>
            </div>
            <div>
                <label>Vai trò:</label>
                <select name="role" required>
                    <option value="">Chọn vai trò</option>
                    <option value="admin">Admin</option>
                    <option value="teacher">Giảng viên</option>
                    <option value="student">Sinh viên</option>
                </select>
            </div>
            <div>
                <label>Đã phê duyệt:</label>
                <input type="checkbox" name="approved" value="true">
            </div>
            <div class="action-buttons">
                <button type="button" class="add-btn">Thêm</button>
                <button type="button" class="edit-btn">Sửa</button>
                <button type="button" class="delete-btn">Xóa</button>
            </div>
        </form>

        <div class="search-container">
            <input type="text" class="search-input" placeholder="Tìm theo tên hoặc email...">
        </div>
        <div class="accounts-table">
            <table>
                <thead>
                    <tr>
                        <th>Tên người dùng</th>
                        <th>Email</th>
                        <th>Vai trò</th>
                        <th>Đã phê duyệt</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        List<Account> accounts = (List<Account>) request.getAttribute("accounts");
                        if (accounts != null && !accounts.isEmpty()) {
                            for (Account account : accounts) {
                    %>
                        <tr data-account-id="<%= account.getAccountId() %>" data-password="<%= account.getPassword() != null ? account.getPassword() : "" %>">
                            <td><%= account.getUsername() != null ? account.getUsername() : "" %></td>
                            <td><%= account.getEmail() != null ? account.getEmail() : "" %></td>
                            <td><%= account.getRole() != null ? account.getRole() : "" %></td>
                            <td><%= account.isApproved() ? "✓" : "✗" %></td>
                        </tr>
                    <% 
                            }
                        } else {
                    %>
                        <tr><td colspan="4">Không có tài khoản nào.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>

        <div class="back-button-container">
            <a href="home.jsp"><button class="back-button">Quay lại</button></a>
        </div>
    </main>

    <footer class="footer">
        <p>© 2025 Trường Đại Học Công Nghệ ĐÔNG Á</p>
    </footer>
</body>
</html>