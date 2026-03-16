<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Student" %>
<%@ page import="model.ql_class" %>
<%@ page import="model.Account" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Sinh Viên</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        .form-container { padding: 15px; display: flex; flex-wrap: wrap; gap: 15px; background-color: white; border-radius: 0.5rem; box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.1); margin-bottom: 20px; }
        .form-container > div { flex: 1 1 200px; }
        .form-container label { display: block; font-size: 0.9rem; color: #666; margin-bottom: 5px; }
        .form-container input, .form-container select { width: 100%; padding: 6px 8px; border: 1px solid #ccc; border-radius: 4px; font-size: 0.9rem; box-sizing: border-box; line-height: 1.5; }
        .action-buttons { display: flex; gap: 10px; justify-content: flex-start; margin-top: 10px; }
        .action-buttons button { padding: 4px 10px; font-size: 0.8rem; border: none; border-radius: 0.25rem; cursor: pointer; transition: background-color 0.3s ease; line-height: 1.5; }
        .action-buttons .add-btn { background-color: #28a745; color: white; }
        .action-buttons .add-btn:hover { background-color: #218838; }
        .action-buttons .edit-btn, .action-buttons .delete-btn { background-color: #ccc; color: #333; pointer-events: none; }
        .action-buttons .edit-btn.active, .action-buttons .delete-btn.active { background-color: #007BFF; color: white; pointer-events: auto; }
        .action-buttons .edit-btn.active:hover { background-color: #0056b3; }
        .action-buttons .delete-btn.active { background-color: #dc3545; }
        .action-buttons .delete-btn.active:hover { background-color: #c82333; }
        .students-table { margin: 20px 0; background-color: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); overflow-x: auto; }
        table { min-width: 600px; width: 100%; border-collapse: collapse; }
        th, td { padding: 6px 8px; text-align: center; border-bottom: 1px solid #ddd; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
        th { background-color: #f2f2f2; height: 30px; line-height: 30px; }
        th:nth-child(1), td:nth-child(1) { width: 15%; }
        th:nth-child(2), td:nth-child(2) { width: 25%; }
        th:nth-child(3), td:nth-child(3) { width: 20%; }
        th:nth-child(4), td:nth-child(4) { width: 20%; }
        th:nth-child(5), td:nth-child(5) { width: 20%; }
        tr.selected { background-color: #e6f3ff; font-weight: bold; }
        tr:hover { background-color: #f5f5f5; }
        .back-button-container { margin-top: 20px; margin-bottom: 20px; }
        .search-container { margin-bottom: 15px; }
        .search-container input { padding: 8px; width: 200px; border: 1px solid #ccc; border-radius: 4px; font-size: 0.9rem; }
        .error { color: red; margin-bottom: 10px; display: none; }
        .success { color: green; margin-bottom: 10px; display: none; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; }
        .success { background-color: #d4edda; color: #155724; }
        .error { background-color: #f8d7da; color: #721c24; }
    </style>
    <script>
        document.addEventListener("DOMContentLoaded", function() {
            const rows = document.querySelectorAll("table tbody tr");
            let selectedRow = null;

            rows.forEach(row => {
                row.addEventListener("click", function() {
                    if (selectedRow) selectedRow.classList.remove("selected");
                    this.classList.add("selected");
                    selectedRow = this;

                    const studentId = this.cells[0].textContent;
                    const name = this.cells[1].textContent;
                    const department = this.cells[2].textContent;
                    const classId = this.cells[3].dataset.classId;
                    const birthDate = this.cells[4].textContent;
                    const gender = this.cells[5].textContent;

                    const form = document.querySelector(".form-container");
                    form.querySelector('input[name="action"]').value = "update";
                    form.querySelector('input[name="student_id"]').value = studentId;
                    form.querySelector('input[name="name"]').value = name;
                    form.querySelector('input[name="department"]').value = department;
                    form.querySelector('select[name="class_id"]').value = classId;
                    form.querySelector('input[name="birth_date"]').value = birthDate;
                    form.querySelector('select[name="gender"]').value = gender;

                    document.querySelector(".edit-btn").classList.add("active");
                    document.querySelector(".delete-btn").classList.add("active");
                });
            });

            document.querySelector(".add-btn").addEventListener("click", function() {
                if (validateForm()) {
                    const form = document.querySelector(".form-container");
                    form.querySelector('input[name="action"]').value = "add";
                    form.submit();
                }
            });

            document.querySelector(".edit-btn").addEventListener("click", function() {
                if (!this.classList.contains("active") || !selectedRow) {
                    showMessage("error", "Vui lòng chọn một sinh viên để sửa!");
                    return;
                }
                if (validateForm()) {
                    document.querySelector(".form-container").submit();
                }
            });

            document.querySelector(".delete-btn").addEventListener("click", function() {
                if (!this.classList.contains("active") || !selectedRow) {
                    showMessage("error", "Vui lòng chọn một sinh viên để xóa!");
                    return;
                }
                const studentId = selectedRow.cells[0].textContent;
                if (confirm("Bạn có chắc muốn xóa sinh viên này?")) {
                    const deleteForm = document.querySelector(".delete-form");
                    deleteForm.querySelector('input[name="student_id"]').value = studentId;
                    deleteForm.submit();
                }
            });

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

            function validateForm() {
                const form = document.querySelector(".form-container");
                const studentId = form.querySelector('input[name="student_id"]').value;
                const name = form.querySelector('input[name="name"]').value;
                const department = form.querySelector('input[name="department"]').value;
                const classId = form.querySelector('select[name="class_id"]').value;
                const birthDate = form.querySelector('input[name="birth_date"]').value;
                const gender = form.querySelector('select[name="gender"]').value;

                if (!studentId || !name || !department || !classId || !birthDate || !gender) {
                    showMessage("error", "Vui lòng điền đầy đủ tất cả các trường!");
                    return false;
                }
                if (!/^\d{4}-\d{2}-\d{2}$/.test(birthDate)) {
                    showMessage("error", "Ngày sinh phải có định dạng yyyy-MM-dd!");
                    return false;
                }
                return true;
            }

            function showMessage(type, message) {
                const messageContainer = document.querySelector(`.${type}`);
                messageContainer.textContent = message;
                messageContainer.style.display = "block";
                setTimeout(() => {
                    messageContainer.style.display = "none";
                }, 5000);
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
            <a href="StudentServlet" class="active">Sinh Viên</a>
            <a href="ContactServlet">Liên Hệ</a>
            <a href="AccountServlet">Tài Khoản</a>
            <a href="TeacherServlet">Giảng Viên</a>
            <a href="ClassServlet">Lớp Học</a>
            <a href="ReportServlet">Báo cáo</a>
            <a href="logout" class="logout">Đăng Xuất</a>
        </div>
    </nav>

    <main class="main">
        <h2 class="welcome">Quản lý sinh viên</h2>
        <div class="message success" style="display: <%= request.getAttribute("successMessage") != null ? "block" : "none" %>;">
            <%= request.getAttribute("successMessage") != null ? request.getAttribute("successMessage") : "" %>
        </div>
        <div class="message error" style="display: <%= request.getAttribute("errorMessage") != null ? "block" : "none" %>;">
            <%= request.getAttribute("errorMessage") != null ? request.getAttribute("errorMessage") : "" %>
        </div>

        <form action="StudentServlet" method="post" class="form-container">
            <input type="hidden" name="action" value="add">
            <div>
                <label>Mã sinh viên:</label>
                <input type="text" name="student_id" required>
            </div>
            <div>
                <label>Họ và tên:</label>
                <input type="text" name="name" required>
            </div>
            <div>
                <label>Khoa:</label>
                <input type="text" name="department" required>
            </div>
            <div>
                <label>Tên lớp:</label>
                <select name="class_id" required>
                    <option value="">Chọn lớp</option>
                    <% 
                        List<ql_class> classes = (List<ql_class>) request.getAttribute("classes");
                        if (classes != null) {
                            for (ql_class cls : classes) {
                    %>
                    <option value="<%= cls.getClassId() %>"><%= cls.getClassName() %></option>
                    <% 
                            }
                        }
                    %>
                </select>
            </div>
            <div>
                <label>Ngày sinh:</label>
                <input type="date" name="birth_date" required>
            </div>
            <div>
                <label>Giới tính:</label>
                <select name="gender" required>
                    <option value="">Chọn giới tính</option>
                    <option value="Nam">Nam</option>
                    <option value="Nữ">Nữ</option>
                </select>
            </div>
            <div class="action-buttons">
                <button type="button" class="add-btn">Thêm</button>
                <button type="button" class="edit-btn">Sửa</button>
                <button type="button" class="delete-btn">Xóa</button>
            </div>
        </form>

        <form action="StudentServlet" method="post" class="delete-form" style="display: none;">
            <input type="hidden" name="action" value="delete">
            <input type="hidden" name="student_id">
        </form>

        <div class="search-container">
            <input type="text" class="search-input" placeholder="Tìm mã hoặc tên sinh viên...">
        </div>
        <div class="students-table">
            <table>
                <thead>
                    <tr>
                        <th>Mã SV</th>
                        <th>Họ và tên</th>
                        <th>Khoa</th>
                        <th>Tên lớp</th>
                        <th>Ngày sinh</th>
                        <th>Giới tính</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        List<Student> students = (List<Student>) request.getAttribute("students");
                        if (students != null && !students.isEmpty()) {
                            for (Student student : students) {
                    %>
                    <tr>
                        <td><%= student.getStudentId() != null ? student.getStudentId() : "" %></td>
                        <td><%= student.getName() != null ? student.getName() : "" %></td>
                        <td><%= student.getDepartment() != null ? student.getDepartment() : "" %></td>
                        <td data-class-id="<%= student.getClassId() %>">
                            <%= student.getClassName() != null ? student.getClassName() : "" %>
                        </td>
                        <td><%= student.getBirthDate() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(student.getBirthDate()) : "" %></td>
                        <td><%= student.getGender() != null ? student.getGender() : "" %></td>
                    </tr>
                    <% 
                            }
                        } else {
                    %>
                    <tr><td colspan="6">Không có sinh viên nào.</td></tr>
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