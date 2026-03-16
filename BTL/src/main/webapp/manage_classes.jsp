<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.ql_class" %>
<%@ page import="model.Subject" %>
<%@ page import="model.Teacher" %>
<%@ page import="model.Account" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Lớp Học</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        .form-container { padding: 15px; display: flex; flex-wrap: wrap; gap: 15px; background-color: white; border-radius: 0.5rem; box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.1); margin-bottom: 20px; }
        .form-container > div { flex: 1 1 200px; }
        .form-container label { display: block; font-size: 0.9rem; color: #666; margin-bottom: 5px; }
        .form-container input, .form-container select { width: 100%; padding: 6px 8px; border: 1px solid #ccc; border-radius: 4px; font-size: 0.9rem; box-sizing: border-box; line-height: 1.5; }
        .action-buttons { display: flex; gap: 10px; justify-content: flex-start; margin-top: 10px; }
        .action-buttons button { padding: 4px 10px; font-size: 0.8rem; border: none; border-radius: 0.25rem; cursor: pointer; transition: background-color 0.3s ease; line-height: 1.5; margin-top: 13px; height: 30px; }
        .action-buttons .add-btn { background-color: #28a745; color: white; }
        .action-buttons .add-btn:hover { background-color: #218838; }
        .action-buttons .edit-btn, .action-buttons .delete-btn { background-color: #ccc; color: #333; pointer-events: none; }
        .action-buttons .edit-btn.active, .action-buttons .delete-btn.active { background-color: #007BFF; color: white; pointer-events: auto; }
        .action-buttons .edit-btn.active:hover { background-color: #0056b3; }
        .action-buttons .delete-btn.active { background-color: #dc3545; }
        .action-buttons .delete-btn.active:hover { background-color: #c82333; }
        .classes-table { margin: 20px 0; background-color: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); overflow-x: hidden; }
        table { min-width: 800px; width: 100%; border-collapse: collapse; }
        th, td { padding: 6px 8px; text-align: center; border-bottom: 1px solid #ddd; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
        th { background-color: #f2f2f2; height: 30px; line-height: 30px; }
        th:nth-child(1), td:nth-child(1) { width: 9%; }
        th:nth-child(2), td:nth-child(2) { width: 20%; }
        th:nth-child(3), td:nth-child(3) { width: 30%; white-space: normal; }
        th:nth-child(4), td:nth-child(4) { width: 25%; }
        tr.selected { background-color: #e6f3ff; font-weight: bold; }
        tr:hover { background-color: #f5f5f5; }
        .back-button-container { margin-top: 20px; margin-bottom: 20px; }
        .search-container { margin-bottom: 15px; }
        .search-container input { padding: 8px; width: 200px; border: 1px solid #ccc; border-radius: 4px; font-size: 0.9rem; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; }
        .success { background-color: #d4edda; color: #155724; }
        .error { background-color: #f8d7da; color: #721c24; }
        .assign-subject-container {
            width: auto;
            padding: 15px;
            background-color: white;
            border-radius: 0.5rem;
            box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.1);
            margin-bottom: 20px;
            display: none;
        }
        .assign-subject-container.active { display: block; }
        .assign-subject-container > div { margin-bottom: 15px; }
        .assign-subject-container label { display: block; font-size: 0.9rem; color: #666; margin-bottom: 5px; }
        .assign-subject-container select {
            width: 100%;
            padding: 6px 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 0.9rem;
            box-sizing: border-box;
        }
        .assign-subject-container .action-buttons button {
            padding: 6px 12px;
            font-size: 0.9rem;
            border: none;
            border-radius: 0.25rem;
            cursor: pointer;
        }
        .assign-subject-container .action-buttons .submit-btn {
            background-color: #28a745;
            color: white;
        }
        .assign-subject-container .action-buttons .submit-btn:hover {
            background-color: #218838;
        }
        .assign-subject-container .action-buttons .cancel-btn {
            background-color: #dc3545;
            color: white;
        }
        .assign-subject-container .action-buttons .cancel-btn:hover {
            background-color: #c82333;
        }
        .toggle-button {
            padding: 6px 12px;
            font-size: 0.9rem;
            background-color: #007BFF;
            color: white;
            border: none;
            border-radius: 0.25rem;
            cursor: pointer;
            margin-bottom: 15px;
        }
        .toggle-button:hover {
            background-color: #0056b3;
        }
        .adu {
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 4px;
            text-align: center;
            display: flex;
            justify-content: center;
        }
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

                const classId = this.cells[0].textContent;
                const className = this.cells[1].textContent;
                const subjectName = this.cells[2].dataset.subjectId;
                const lecturer = this.cells[3].textContent;

                const form = document.querySelector(".form-container");
                form.querySelector('input[name="action"]').value = "update";
                form.querySelector('input[name="class_id"]').value = classId;
                form.querySelector('input[name="class_name"]').value = className;

                const subjectSelect = form.querySelector('select[name="subject_id"]');
                subjectSelect.value = subjectName || "";

                const teacherSelect = form.querySelector('select[name="teacher_id"]');
                Array.from(teacherSelect.options).forEach(option => {
                    if (option.text === lecturer) {
                        teacherSelect.value = option.value;
                    }
                });

                const editBtn = document.querySelector(".edit-btn");
                const deleteBtn = document.querySelector(".delete-btn");
                editBtn.classList.add("active");
                deleteBtn.classList.add("active");
            });
        });

        document.querySelector(".add-btn").addEventListener("click", function() {
            const form = document.querySelector(".form-container");
            const className = form.querySelector('input[name="class_name"]').value;
            const subjectId = form.querySelector('select[name="subject_id"]').value;

            if (!className || !subjectId) {
                alert("Vui lòng điền đầy đủ thông tin bắt buộc!");
                return;
            }

            form.querySelector('input[name="action"]').value = "add";
            form.querySelector('input[name="class_id"]').value = "";
            form.submit();
        });

        document.querySelector(".edit-btn").addEventListener("click", function() {
            if (this.classList.contains("active") && selectedRow) {
                const form = document.querySelector(".form-container");
                const className = form.querySelector('input[name="class_name"]').value;
                const subjectId = form.querySelector('select[name="subject_id"]').value;

                if (!className || !subjectId) {
                    alert("Vui lòng điền đầy đủ thông tin bắt buộc!");
                    return;
                }

                form.submit();
            } else {
                alert("Vui lòng chọn một dòng để sửa!");
            }
        });

        document.querySelector(".delete-btn").addEventListener("click", function() {
            if (this.classList.contains("active") && selectedRow) {
                const form = document.querySelector(".form-container");
                const classId = selectedRow.cells[0].textContent;

                if (confirm("Xóa lớp học này sẽ xóa tất cả dữ liệu liên quan. Bạn có chắc muốn xóa?")) {
                    form.querySelector('input[name="action"]').value = "delete";
                    form.querySelector('input[name="class_id"]').value = classId;
                    form.querySelector('input[name="class_name"]').value = "";
                    form.querySelector('select[name="subject_id"]').value = "";
                    form.querySelector('select[name="teacher_id"]').value = "";
                    form.submit();
                }
            } else {
                alert("Vui lòng chọn một dòng để xóa!");
            }
        });

        document.querySelector(".search-input").addEventListener("input", function() {
            const searchValue = this.value.toLowerCase();
            rows.forEach(row => {
                const classId = row.cells[0].textContent.toLowerCase();
                const className = row.cells[1].textContent.toLowerCase();
                if (classId.includes(searchValue) || className.includes(searchValue)) {
                    row.style.display = "";
                } else {
                    row.style.display = "none";
                }
            });
        });

        document.querySelector(".toggle-assign-subject").addEventListener("click", function() {
            const assignForm = document.querySelector(".assign-subject-container");
            assignForm.classList.toggle("active");
            this.textContent = assignForm.classList.contains("active") ? "Ẩn Form Thêm Môn Học" : "Thêm Môn Học Cho Lớp";
        });

        document.querySelector(".cancel-btn").addEventListener("click", function() {
            const assignForm = document.querySelector(".assign-subject-container");
            assignForm.classList.remove("active");
            document.querySelector(".toggle-assign-subject").textContent = "Thêm Môn Học Cho Lớp";
        });

        // Hiển thị form và giữ giá trị nếu có lỗi
        <% if (request.getAttribute("errorMessage") != null && request.getAttribute("selectedClassId") != null) { %>
            const assignForm = document.querySelector(".assign-subject-container");
            assignForm.classList.add("active");
            document.querySelector(".toggle-assign-subject").textContent = "Ẩn Form Thêm Môn Học";
            const classSelect = assignForm.querySelector('select[name="class_id"]');
            const subjectSelect = assignForm.querySelector('select[name="subject_id"]');
            classSelect.value = "<%= request.getAttribute("selectedClassId") %>";
            subjectSelect.value = "<%= request.getAttribute("selectedSubjectId") %>";
        <% } %>
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
            <a href="StudentServlet">Sinh Viên</a>
            <a href="ContactServlet">Liên Hệ</a>
            <a href="AccountServlet">Tài Khoản</a>
            <a href="TeacherServlet">Giảng Viên</a>
            <a href="ClassServlet" class="active">Lớp Học</a>
            <a href="ReportServlet">Báo cáo</a>
            <a href="logout" class="logout">Đăng Xuất</a>
        </div>
    </nav>

    <main class="main">
        <h2 class="welcome">Quản lý lớp học</h2>
        <% if (request.getAttribute("errorMessage") != null) { %>
            <p class="message error"><%= request.getAttribute("errorMessage") %></p>
        <% } else if (request.getAttribute("successMessage") != null) { %>
            <p class="message success"><%= request.getAttribute("successMessage") %></p>
        <% } %>

        <form action="ClassServlet" method="post" class="form-container">
            <input type="hidden" name="action" value="add">
            <input type="hidden" name="class_id">
            <div>
                <label>Tên lớp:</label>
                <input type="text" name="class_name" required>
            </div>
            <div>
                <label>Môn học:</label>
                <select name="subject_id" required>
                    <option value="">-- Chọn môn học --</option>
                    <% 
                        List<Subject> subjects = (List<Subject>) request.getAttribute("subjects");
                        if (subjects != null) {
                            for (Subject subject : subjects) {
                    %>
                        <option value="<%= subject.getSubjectId() %>"><%= subject.getSubjectName() %></option>
                    <% 
                            }
                        }
                    %>
                </select>
            </div>
            <div>
                <label>Giảng viên:</label>
                <select name="teacher_id">
                    <option value="">-- Chọn giảng viên --</option>
                    <% 
                        List<Teacher> teachers = (List<Teacher>) request.getAttribute("teachers");
                        if (teachers != null) {
                            for (Teacher teacher : teachers) {
                    %>
                        <option value="<%= teacher.getTeacherId() %>"><%= teacher.getName() %></option>
                    <% 
                            }
                        }
                    %>
                </select>
            </div>
            <div class="action-buttons">
                <button type="button" class="add-btn">Thêm</button>
                <button type="button" class="edit-btn">Sửa</button>
                <button type="button" class="delete-btn">Xóa</button>
            </div>
        </form>

        <div class="adu">
            <button class="toggle-assign-subject">Thêm Môn Học Cho Lớp</button>
        </div>

        <form action="ClassServlet" method="post" class="assign-subject-container">
            <input type="hidden" name="action" value="assign_subject">
            <div>
                <label>Chọn lớp học:</label>
                <select name="class_id" required>
                    <option value="">Chọn lớp</option>
                    <% 
                        List<ql_class> classes = (List<ql_class>) request.getAttribute("classes");
                        if (classes != null) {
                            for (ql_class cls : classes) {
                                String selected = cls.getClassId().equals(request.getAttribute("selectedClassId")) ? "selected" : "";
                    %>
                        <option value="<%= cls.getClassId() %>" <%= selected %>><%= cls.getClassName() %></option>
                    <% 
                            }
                        }
                    %>
                </select>
            </div>
            <div>
                <label>Chọn môn học:</label>
                <select name="subject_id" required>
                    <option value="">Chọn môn học</option>
                    <% 
                        if (subjects != null) {
                            for (Subject subject : subjects) {
                                String selected = subject.getSubjectId().equals(request.getAttribute("selectedSubjectId")) ? "selected" : "";
                    %>
                        <option value="<%= subject.getSubjectId() %>" <%= selected %>><%= subject.getSubjectName() %></option>
                    <% 
                            }
                        }
                    %>
                </select>
            </div>
            <div class="action-buttons">
                <button type="submit" class="submit-btn">Thêm</button>
                <button type="button" class="cancel-btn">Hủy</button>
            </div>
        </form>

        <div class="search-container">
            <input type="text" class="search-input" placeholder="Tìm theo mã hoặc tên lớp...">
        </div>
        <div class="classes-table">
            <table>
                <thead>
                    <tr>
                        <th>Mã lớp</th>
                        <th>Tên lớp</th>
                        <th>Môn học</th>
                        <th>Giảng viên</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        if (classes != null && !classes.isEmpty()) {
                            for (ql_class classObj : classes) {
                    %>
                        <tr>
                            <td><%= classObj.getClassId() != null ? classObj.getClassId() : "" %></td>
                            <td><%= classObj.getClassName() != null ? classObj.getClassName() : "" %></td>
                            <td data-subject-id="<%= classObj.getSubjectId() %>">
                                <%= classObj.getAllSubjects() != null ? String.join("<br>", classObj.getAllSubjects()) : "" %>
                            </td>
                            <td><%= classObj.getLecturer() != null ? classObj.getLecturer() : "" %></td>
                        </tr>
                    <% 
                            }
                        } else {
                    %>
                        <tr><td colspan="4">Không có lớp học nào.</td></tr>
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