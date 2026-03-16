<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Teacher" %>
<%@ page import="model.Account" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Giảng Viên</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .success { background-color: #d4edda; color: #155724; }
        .error { background-color: #f8d7da; color: #721c24; }
        .form-container { padding: 15px; display: flex; flex-wrap: wrap; gap: 15px; background-color: white; border-radius: 0.5rem; box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.1); margin-bottom: 20px; }
        .form-container > div { flex: 1 1 200px; }
        .form-container label { display: block; font-size: 0.9rem; color: #666; margin-bottom: 5px; }
        .form-container input { width: 100%; padding: 6px 8px; border: 1px solid #ccc; border-radius: 4px; font-size: 0.9rem; box-sizing: border-box; line-height: 1.5; }
        .action-buttons { display: flex; gap: 10px; justify-content: flex-start; margin-top: 10px; }
        .action-buttons button { padding: 4px 10px; font-size: 0.8rem; border: none; border-radius: 0.25rem; cursor: pointer; transition: background-color 0.3s ease; line-height: 1.5; height: 30px; margin-top: 13px; }
        .action-buttons .add-btn { background-color: #28a745; color: white; }
        .action-buttons .add-btn:hover { background-color: #218838; }
        .action-buttons .edit-btn, .action-buttons .delete-btn { background-color: #ccc; color: #333; pointer-events: none; }
        .action-buttons .edit-btn.active, .action-buttons .delete-btn.active { background-color: #007BFF; color: white; pointer-events: auto; }
        .action-buttons .edit-btn.active:hover { background-color: #0056b3; }
        .action-buttons .delete-btn.active { background-color: #dc3545; }
        .action-buttons .delete-btn.active:hover { background-color: #c82333; }
        .teachers-table { margin: 20px 0; background-color: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0,0, 0.1); overflow-x: auto; }
        table { min-width: 800px; width: 100%; border-collapse: collapse; }
        th, td { padding: 6px 8px; text-align: center; border-bottom: 1px solid #ddd; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
        th { background-color: #f2f2f2; height: 30px; line-height: 30px; }
        th:nth-child(1), td:nth-child(1) { width: 15%; }
        th:nth-child(2), td:nth-child(2) { width: 25%; }
        th:nth-child(3), td:nth-child(3) { width: 25%; }
        th:nth-child(4), td:nth-child(4) { width: 25%; }
        tr.selected { background-color: #e6f3ff; font-weight: bold; }
        tr:hover { background-color: #f5f5f5; }
        .back-button-container { margin-top: 20px; margin-bottom: 20px; }
        .search-container { margin-bottom: 15px; }
        .search-container input { padding: 8px; width: 200px; border: 1px solid #ccc; border-radius: 4px; font-size: 0.9rem; }
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

                    const teacherId = this.cells[0].textContent.trim();
                    const name = this.cells[1].textContent.trim();
                    const email = this.cells[2].textContent.trim();
                    const phone = this.cells[3].textContent.trim();

                    console.log("Selected row with teacher ID: " + teacherId); // Debug

                    const form = document.querySelector(".form-container");
                    form.querySelector('input[name="action"]').value = "update";
                    form.querySelector('input[name="teacher_id"]').value = teacherId;
                    form.querySelector('input[name="name"]').value = name;
                    form.querySelector('input[name="email"]').value = email;
                    form.querySelector('input[name="phone"]').value = phone;

                    const editBtn = document.querySelector(".edit-btn");
                    const deleteBtn = document.querySelector(".delete-btn");
                    editBtn.classList.add("active");
                    deleteBtn.classList.add("active");
                });
            });

            document.querySelector(".add-btn").addEventListener("click", function() {
                const form = document.querySelector(".form-container");
                const name = form.querySelector('input[name="name"]').value.trim();
                const email = form.querySelector('input[name="email"]').value.trim();
                const phone = form.querySelector('input[name="phone"]').value.trim();

                if (!name) {
                    alert("Vui lòng điền họ tên!");
                    return;
                }

                form.querySelector('input[name="action"]').value = "add";
                form.submit();
            });

            document.querySelector(".edit-btn").addEventListener("click", function() {
                if (!this.classList.contains("active") || !selectedRow) {
                    alert("Vui lòng chọn một dòng để sửa!");
                    return;
                }

                const form = document.querySelector(".form-container");
                const teacherId = form.querySelector('input[name="teacher_id"]').value.trim();
                const name = form.querySelector('input[name="name"]').value.trim();

                if (!teacherId || isNaN(teacherId) || !name) {
                    alert("Vui lòng điền mã giảng viên và họ tên!");
                    return;
                }

                form.querySelector('input[name="action"]').value = "update";
                form.submit();
            });

            document.querySelector(".delete-btn").addEventListener("click", function() {
                if (!this.classList.contains("active") || !selectedRow) {
                    alert("Vui lòng chọn một dòng để xóa!");
                    return;
                }

                const teacherId = selectedRow.cells[0].textContent.trim();
                console.log("Attempting to delete teacher ID: " + teacherId); // Debug

                if (!teacherId || isNaN(teacherId) || teacherId === "") {
                    alert("Mã giảng viên không hợp lệ hoặc không được để trống!");
                    console.error("Invalid teacher ID: " + teacherId); // Debug
                    return;
                }

                if (confirm("Bạn có chắc muốn xóa giảng viên này và tất cả dữ liệu liên quan?")) {
                    const form = document.createElement("form");
                    form.method = "POST";
                    form.action = "TeacherServlet";

                    // Tạo input cho action
                    const actionInput = document.createElement("input");
                    actionInput.type = "hidden";
                    actionInput.name = "action";
                    actionInput.value = "delete";
                    form.appendChild(actionInput);

                    // Tạo input cho teacher_id
                    const idInput = document.createElement("input");
                    idInput.type = "hidden";
                    idInput.name = "teacher_id";
                    idInput.value = teacherId;
                    form.appendChild(idInput);

                    console.log("Submitting delete form with teacher ID: " + teacherId); // Debug
                    document.body.appendChild(form);
                    form.submit();
                }
            });

            document.querySelector(".search-input").addEventListener("input", function() {
                const searchValue = this.value.toLowerCase();
                rows.forEach(row => {
                    const teacherId = row.cells[0].textContent.toLowerCase();
                    const name = row.cells[1].textContent.toLowerCase();
                    if (teacherId.includes(searchValue) || name.includes(searchValue)) {
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
            <a href="AccountServlet">Tài Khoản</a>
            <a href="TeacherServlet" class="active">Giảng Viên</a>
            <a href="ClassServlet">Lớp Học</a>
            <a href="ReportServlet">Báo cáo</a>
            <a href="logout" class="logout">Đăng Xuất</a>
        </div>
    </nav>

    <main class="main">
        <h2 class="welcome">Quản lý giảng viên</h2>
        <% if (request.getAttribute("errorMessage") != null) { %>
            <p class="message error"><%= request.getAttribute("errorMessage") %></p>
        <% } %>
        <% if (request.getAttribute("successMessage") != null) { %>
            <p class="message success"><%= request.getAttribute("successMessage") %></p>
        <% } %>

        <form action="TeacherServlet" method="post" class="form-container">
            <input type="hidden" name="action" value="add">
            <input type="hidden" name="teacher_id" value="<%= request.getAttribute("form_teacher_id") != null ? request.getAttribute("form_teacher_id") : "" %>">
            <div>
                <label>Họ và tên:</label>
                <input type="text" name="name" value="<%= request.getAttribute("form_name") != null ? request.getAttribute("form_name") : "" %>" required>
            </div>
            <div>
                <label>Email:</label>
                <input type="text" name="email" value="<%= request.getAttribute("form_email") != null ? request.getAttribute("form_email") : "" %>">
            </div>
            <div>
                <label>Số điện thoại:</label>
                <input type="text" name="phone" value="<%= request.getAttribute("form_phone") != null ? request.getAttribute("form_phone") : "" %>">
            </div>
            <div class="action-buttons">
                <button type="button" class="add-btn">Thêm</button>
                <button type="button" class="edit-btn">Sửa</button>
                <button type="button" class="delete-btn">Xóa</button>
            </div>
        </form>

        <div class="search-container">
            <input type="text" class="search-input" placeholder="Tìm theo mã hoặc tên giảng viên...">
        </div>
        <div class="teachers-table">
            <table>
                <thead>
                    <tr>
                        <th>Mã giảng viên</th>
                        <th>Họ và tên</th>
                        <th>Email</th>
                        <th>Số điện thoại</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        List<Teacher> teachers = (List<Teacher>) request.getAttribute("teachers");
                        if (teachers != null && !teachers.isEmpty()) {
                            for (Teacher teacher : teachers) {
                                // Kiểm tra teacherId không null và hợp lệ
                                String teacherId = teacher.getTeacherId() > 0 ? String.valueOf(teacher.getTeacherId()) : "";
                                System.out.println("Rendering teacher ID: " + teacherId); // Debug server-side
                    %>
                        <tr>
                            <td data-teacher-id="<%= teacherId %>"><%= teacherId %></td>
                            <td><%= teacher.getName() != null ? teacher.getName() : "" %></td>
                            <td><%= teacher.getEmail() != null ? teacher.getEmail() : "" %></td>
                            <td><%= teacher.getPhone() != null ? teacher.getPhone() : "" %></td>
                        </tr>
                    <% 
                            }
                        } else {
                    %>
                        <tr><td colspan="4">Không có giảng viên nào.</td></tr>
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