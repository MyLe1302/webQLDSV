<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Subject" %>
<%@ page import="model.Account" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh Sách Môn Học</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        .message { padding: 10px; margin: 10px 0; border-radius: 4px; text-align: center; }
        .success { background-color: #d4edda; color: #155724; }
        .error { background-color: #f8d7da; color: #721c24; }
        .form-container {
            padding: 15px;
            display: flex;
            flex-wrap: wrap;
            gap: 15px;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            margin-bottom: 20px;
        }
        .form-container > div {
            flex: 1 1 200px;
        }
        .form-container label {
            display: block;
            font-size: 0.9rem;
            color: #333;
            margin-bottom: 5px;
        }
        .form-container input {
            width: 100%;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 0.9rem;
        }
        .action-buttons {
            display: flex; 
            gap: 10px; 
            justify-content: flex-start; 
            margin-top: 10px;
        }
        .action-buttons button {
            padding: 4px 10px;
            font-size: 0.8rem; 
            border: none; 
            border-radius: 0.25rem; 
            cursor: pointer; 
            transition: background-color 0.3s ease; 
            line-height: 1.5; 
            margin-top: 13px; 
            height: 30px;
            
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
            background-color: #007bff;
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
        .subjects-table {
            margin: 20px 0;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            overflow-x: auto;
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            padding: 10px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #f2f2f2;
            font-weight: bold;
        }
        tr:hover {
            background-color: #f5f5f5;
        }
        tr.selected {
            background-color: #e6f3ff;
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
        .back-button-container {
            margin: 20px 0;
        }
        .back-button {
            padding: 8px 15px;
            background-color: #6c757d;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .back-button:hover {
            background-color: #5a6268;
        }
    </style>
    <script>
        document.addEventListener("DOMContentLoaded", function() {
            const rows = document.querySelectorAll("table tbody tr");
            let selectedRow = null;

            <% if (session.getAttribute("account") != null && "admin".equals(((Account) session.getAttribute("account")).getRole())) { %>
            // Xử lý chọn hàng (chỉ cho admin)
            rows.forEach(row => {
                row.addEventListener("click", function() {
                    if (selectedRow) selectedRow.classList.remove("selected");
                    this.classList.add("selected");
                    selectedRow = this;

                    const subjectId = this.cells[0].textContent.trim();
                    const subjectName = this.cells[1].textContent.trim();
                    const credits = this.cells[2].textContent.trim();

                    const form = document.querySelector(".form-container");
                    form.querySelector('input[name="action"]').value = "update";
                    form.querySelector('input[name="subject_id"]').value = subjectId;
                    form.querySelector('input[name="subject_name"]').value = subjectName;
                    form.querySelector('input[name="credits"]').value = credits;

                    document.querySelector(".edit-btn").classList.add("active");
                    document.querySelector(".delete-btn").classList.add("active");
                });
            });

            // Nút Thêm
            document.querySelector(".add-btn").addEventListener("click", function(e) {
                e.preventDefault();
                const form = document.querySelector(".form-container");
                const subjectId = form.querySelector('input[name="subject_id"]').value.trim();
                const subjectName = form.querySelector('input[name="subject_name"]').value.trim();
                const credits = form.querySelector('input[name="credits"]').value.trim();

                if (!subjectId || !subjectName || !credits) {
                    alert("Vui lòng nhập đầy đủ thông tin!");
                    return;
                }

                form.querySelector('input[name="action"]').value = "add";
                form.querySelector(".edit-btn").classList.remove("active");
                form.querySelector(".delete-btn").classList.remove("active");
                if (selectedRow) selectedRow.classList.remove("selected");
                selectedRow = null;

                form.submit();
            });

            // Nút Sửa
            document.querySelector(".edit-btn").addEventListener("click", function() {
                if (this.classList.contains("active") && selectedRow) {
                    const form = document.querySelector(".form-container");
                    const subjectId = form.querySelector('input[name="subject_id"]').value;
                    const subjectName = form.querySelector('input[name="subject_name"]').value;
                    const credits = form.querySelector('input[name="credits"]').value;
                    if (!subjectId || !subjectName || !credits) {
                        alert("Vui lòng nhập đầy đủ thông tin!");
                        return;
                    }
                    form.querySelector('input[name="action"]').value = "update";
                    form.submit();
                } else {
                    alert("Vui lòng chọn một môn học để sửa!");
                }
            });

            // Nút Xóa
            document.querySelector(".delete-btn").addEventListener("click", function() {
                if (this.classList.contains("active") && selectedRow) {
                    if (confirm("Bạn có chắc chắn muốn xóa môn học này?")) {
                        const form = document.querySelector(".form-container");
                        form.querySelector('input[name="action"]').value = "delete";
                        form.submit();
                    }
                } else {
                    alert("Vui lòng chọn một môn học để xóa!");
                }
            });
            <% } %>

            // Tìm kiếm (cho tất cả vai trò)
            const searchInput = document.querySelector(".search-input");
            if (searchInput) {
                searchInput.addEventListener("input", function() {
                    const searchValue = this.value.toLowerCase().trim();
                    rows.forEach(row => {
                        const subjectId = row.cells[0].textContent.trim().toLowerCase();
                        const subjectName = row.cells[1].textContent.trim().toLowerCase();
                        row.style.display = subjectId.includes(searchValue) || subjectName.includes(searchValue) ? "" : "none";
                    });
                });
            }
        });
    </script>
</head>
<body>
    <%
        Account currentUser = (Account) session.getAttribute("account");
        if (currentUser == null) {
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
            <a href="home.jsp">Trang Chủ</a>
            <a href="SubjectServlet" class="active">Môn Học</a>
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
        <h2 class="welcome">Danh Sách Môn Học</h2>
        <% if (request.getAttribute("errorMessage") != null) { %>
            <p class="message error"><%= request.getAttribute("errorMessage") %></p>
        <% } %>
        <% if (request.getAttribute("successMessage") != null) { %>
            <p class="message success"><%= request.getAttribute("successMessage") %></p>
        <% } %>

        <% if ("admin".equals(currentUser.getRole())) { %>
        <form action="SubjectServlet" method="post" class="form-container">
            <input type="hidden" name="action" value="add">
            <div>
                <label>Mã môn học:</label>
                <input type="text" name="subject_id" value="<%= request.getAttribute("subject_id") != null ? request.getAttribute("subject_id") : "" %>" required>
            </div>
            <div>
                <label>Tên môn học:</label>
                <input type="text" name="subject_name" value="<%= request.getAttribute("subject_name") != null ? request.getAttribute("subject_name") : "" %>" required>
            </div>
            <div>
                <label>Số tín chỉ:</label>
                <input type="number" name="credits" value="<%= request.getAttribute("credits") != null ? request.getAttribute("credits") : "" %>" required min="0">
            </div>
            <div class="action-buttons">
                <button type="button" class="add-btn">Thêm</button>
                <button type="button" class="edit-btn">Sửa</button>
                <button type="button" class="delete-btn">Xóa</button>
            </div>
        </form>
        <% } %>

        <div class="search-container">
            <input type="text" class="search-input" placeholder="Tìm theo mã hoặc tên môn học...">
        </div>

        <div class="subjects-table">
            <table>
                <thead>
                    <tr>
                        <th>Mã môn học</th>
                        <th>Tên môn học</th>
                        <th>Số tín chỉ</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        List<Subject> subjects = (List<Subject>) request.getAttribute("subjects");
                        if (subjects != null && !subjects.isEmpty()) {
                            for (Subject subject : subjects) {
                                String subjectId = subject.getSubjectId() != null ? subject.getSubjectId() : "";
                                String subjectName = subject.getSubjectName() != null ? subject.getSubjectName() : "";
                                String credits = subject.getCredits() != null ? subject.getCredits().toString() : "0";
                    %>
                        <tr>
                            <td><%= subjectId %></td>
                            <td><%= subjectName %></td>
                            <td><%= credits %></td>
                        </tr>
                    <% 
                            }
                        } else {
                    %>
                        <tr><td colspan="3">Không có môn học nào.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>

        <div class="back-button-container">
            <a href="home.jsp"><button class="back-button">Quay lại</button></a>
        </div>
    </main>

    <footer class="footer">
        <p>© 2025 Trường Đại Học Công Nghệ Đông Á</p>
    </footer>
</body>
</html>