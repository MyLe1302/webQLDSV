<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Grade" %>
<%@ page import="model.Student" %>
<%@ page import="model.Subject" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Điểm</title>
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
        .form-container input[readonly] {
            background-color: #f9f9f9;
            cursor: not-allowed;
        }
        .action-buttons {
            display: flex;
            gap: 10px;
            justify-content: flex-start;
            margin-top: 10px;
        }
        .action-buttons button {
            padding: 6px 12px;
            font-size: 0.8rem;
            border: none;
            border-radius: 0.25rem;
            cursor: pointer;
            transition: background-color 0.3s ease;
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
        .grades-table {
            margin: 20px 0;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            overflow-x: auto;
        }
        table {
            min-width: 900px;
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
            display: flex;
            margin-top: -15px;
        }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .success { background-color: #d4edda; color: #155724; }
        .error { background-color: #f8d7da; color: #721c24; }
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
    </style>
    <script>
    document.addEventListener("DOMContentLoaded", function() {
        function calculateTotal() {
            const score = parseFloat(document.querySelector('input[name="score"]').value) || 0;
            const attendance = parseFloat(document.querySelector('input[name="attendance"]').value) || 0;
            const midterm = parseFloat(document.querySelector('input[name="midterm"]').value) || 0;
            const finalExam = parseFloat(document.querySelector('input[name="final_exam"]').value) || 0;
            const total = (score * 0.1) + (attendance * 0.1) + (midterm * 0.2) + (finalExam * 0.7);
            document.querySelector('input[name="total"]').value = total.toFixed(1);
            document.querySelector('input[name="letter_grade"]').value = getLetterGrade(total);
            document.querySelector('input[name="note"]').value = getClassification(total);
        }

        function getLetterGrade(total) {
            if (total >= 8.5) return "A";
            else if (total >= 7.0) return "B";
            else if (total >= 5.5) return "C";
            else if (total >= 4.0) return "D";
            else return "F";
        }

        function getClassification(total) {
            if (total >= 8.5) return "Giỏi";
            else if (total >= 7.0) return "Khá";
            else if (total >= 5.5) return "Trung bình";
            else return "Yếu";
        }

        document.querySelectorAll('input[name="score"], input[name="attendance"], input[name="midterm"], input[name="final_exam"]').forEach(input => {
            input.addEventListener("input", calculateTotal);
        });

        const rows = document.querySelectorAll("table tbody tr");
        let selectedRow = null;
        rows.forEach(row => {
            row.addEventListener("click", function() {
                if (selectedRow) selectedRow.classList.remove("selected");
                this.classList.add("selected");
                selectedRow = this;

                const studentId = this.cells[1].textContent;
                const subjectName = this.cells[2].textContent;
                const score = this.cells[3].textContent;
                const attendance = this.cells[4].textContent;
                const midterm = this.cells[5].textContent;
                const finalExam = this.cells[6].textContent;
                const total = this.cells[7].textContent;
                const letterGrade = this.cells[8].textContent === "N/A" ? "" : this.cells[8].textContent;
                const note = this.cells[9].textContent === "N/A" ? "" : this.cells[9].textContent;

                const form = document.querySelector("#gradeForm");
                form.querySelector('input[name="action"]').value = "update";
                form.querySelector('input[name="grade_id"]').value = this.cells[0].textContent;
                form.querySelector('select[name="student_id"]').value = studentId;
                const subjectSelect = form.querySelector('select[name="subject_id"]');
                Array.from(subjectSelect.options).forEach(option => {
                    if (option.textContent === subjectName) {
                        subjectSelect.value = option.value;
                    }
                });
                form.querySelector('input[name="score"]').value = score;
                form.querySelector('input[name="attendance"]').value = attendance;
                form.querySelector('input[name="midterm"]').value = midterm;
                form.querySelector('input[name="final_exam"]').value = finalExam;
                form.querySelector('input[name="total"]').value = total;
                form.querySelector('input[name="letter_grade"]').value = letterGrade;
                form.querySelector('input[name="note"]').value = note;

                document.querySelector(".edit-btn").classList.add("active");
                document.querySelector(".delete-btn").classList.add("active");
            });
        });

        document.querySelector(".add-btn").addEventListener("click", function() {
            const form = document.querySelector("#gradeForm");
            const studentId = form.querySelector('select[name="student_id"]').value;
            const subjectId = form.querySelector('select[name="subject_id"]').value;
            const score = form.querySelector('input[name="score"]').value;
            const attendance = form.querySelector('input[name="attendance"]').value;
            const midterm = form.querySelector('input[name="midterm"]').value;
            const finalExam = form.querySelector('input[name="final_exam"]').value;

            if (!studentId || !subjectId || !score || !attendance || !midterm || !finalExam) {
                alert("Vui lòng điền đầy đủ thông tin!");
                return;
            }

            form.querySelector('input[name="action"]').value = "add";
            form.querySelector('input[name="grade_id"]').value = "";
            form.submit();
        });

        document.querySelector(".edit-btn").addEventListener("click", function() {
            if (this.classList.contains("active") && selectedRow) {
                const form = document.querySelector("#gradeForm");
                const studentId = form.querySelector('select[name="student_id"]').value;
                const subjectId = form.querySelector('select[name="subject_id"]').value;
                const score = form.querySelector('input[name="score"]').value;
                const attendance = form.querySelector('input[name="attendance"]').value;
                const midterm = form.querySelector('input[name="midterm"]').value;
                const finalExam = form.querySelector('input[name="final_exam"]').value;

                if (!studentId || !subjectId || !score || !attendance || !midterm || !finalExam) {
                    alert("Vui lòng điền đầy đủ thông tin!");
                    return;
                }

                form.querySelector('input[name="action"]').value = "update";
                form.submit();
            } else {
                alert("Vui lòng chọn một dòng để sửa!");
            }
        });

        document.querySelector(".delete-btn").addEventListener("click", function() {
            if (this.classList.contains("active") && selectedRow) {
                const gradeId = selectedRow.cells[0].textContent;
                if (!gradeId) {
                    alert("Không thể xóa: Mã điểm không hợp lệ!");
                    return;
                }
                if (confirm("Bạn có chắc muốn xóa điểm này?")) {
                    const form = document.querySelector("#gradeForm");
                    form.querySelector('input[name="action"]').value = "delete";
                    form.querySelector('input[name="grade_id"]').value = gradeId;
                    form.querySelector('select[name="student_id"]').value = "";
                    form.querySelector('select[name="subject_id"]').value = "";
                    form.querySelector('input[name="score"]').value = "";
                    form.querySelector('input[name="attendance"]').value = "";
                    form.querySelector('input[name="midterm"]').value = "";
                    form.querySelector('input[name="final_exam"]').value = "";
                    form.querySelector('input[name="total"]').value = "";
                    form.querySelector('input[name="letter_grade"]').value = "";
                    form.querySelector('input[name="note"]').value = "";
                    form.submit();
                }
            } else {
                alert("Vui lòng chọn một dòng để xóa!");
            }
        });

        document.querySelector(".search-input").addEventListener("input", function() {
            const searchValue = this.value.toLowerCase();
            rows.forEach(row => {
                const studentId = row.cells[1].textContent.toLowerCase();
                const subjectName = row.cells[2].textContent.toLowerCase();
                if (studentId.includes(searchValue) || subjectName.includes(searchValue)) {
                    row.style.display = "";
                } else {
                    row.style.display = "none";
                }
            });
        });

        calculateTotal();
    });
    </script>
</head>
<body>
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
            Integer classId = (Integer) request.getAttribute("classId");
            if (classId == null || classId == -1) {
                String errorMsg = (String) request.getAttribute("errorMessage");
                if (errorMsg == null) {
                    errorMsg = "Vui lòng truy cập qua GradeServlet với class_id hợp lệ (ví dụ: GradeServlet?class_id=1).";
                }
        %>
            <h2 class="welcome">Quản lý điểm - Lớp không xác định</h2>
            <p class="error"><%= errorMsg %></p>
        <% 
            } else {
        %>
            <h2 class="welcome">Quản lý điểm - Lớp <%= classId %></h2>
            <% if (request.getAttribute("errorMessage") != null) { %>
                <p class="message error"><%= request.getAttribute("errorMessage") %></p>
            <% } %>
            <% if (request.getAttribute("successMessage") != null) { %>
                <p class="message success"><%= request.getAttribute("successMessage") %></p>
            <% } %>

            <form id="gradeForm" action="GradeServlet" method="post" class="form-container">
                <input type="hidden" name="action" value="add">
                <input type="hidden" name="class_id" value="<%= classId %>">
                <input type="hidden" name="grade_id" id="grade_id">
                <div>
                    <label>Mã sinh viên:</label>
                    <select name="student_id" required>
                        <option value="">Chọn mã sinh viên</option>
                        <% 
                            List<Student> students = (List<Student>) request.getAttribute("students");
                            if (students != null && !students.isEmpty()) {
                                for (Student student : students) { 
                        %>
                            <option value="<%= student.getStudentId() %>"><%= student.getStudentId() %></option>
                        <% 
                                }
                            } else {
                        %>
                            <option value="">Không có sinh viên</option>
                        <% 
                            }
                        %>
                    </select>
                </div>
                <div>
                    <label>Tên môn học:</label>
                    <select name="subject_id" required>
                        <option value="">Chọn tên môn học</option>
                        <% 
                            List<Subject> subjects = (List<Subject>) request.getAttribute("subjects");
                            if (subjects != null && !subjects.isEmpty()) {
                                for (Subject subject : subjects) { 
                        %>
                            <option value="<%= subject.getSubjectId() %>"><%= subject.getSubjectName() %></option>
                        <% 
                                }
                            } else {
                        %>
                            <option value="">Không có môn học</option>
                        <% 
                            }
                        %>
                    </select>
                </div>
                <div>
                    <label>Điểm quá trình:</label>
                    <input type="number" step="0.1" name="score" required>
                </div>
                <div>
                    <label>Điểm chuyên cần:</label>
                    <input type="number" step="0.1" name="attendance" required>
                </div>
                <div>
                    <label>Điểm giữa kỳ:</label>
                    <input type="number" step="0.1" name="midterm" required>
                </div>
                <div>
                    <label>Điểm cuối kỳ:</label>
                    <input type="number" step="0.1" name="final_exam" required>
                </div>
                <div>
                    <label>Tổng điểm:</label>
                    <input type="number" step="0.1" name="total" readonly required>
                </div>
                <div>
                    <label>Điểm chữ:</label>
                    <input type="text" name="letter_grade" readonly required>
                </div>
                <div>
                    <label>Ghi chú:</label>
                    <input type="text" name="note" readonly>
                </div>
                <div class="action-buttons">
                    <button type="button" class="add-btn">Thêm</button>
                    <button type="button" class="edit-btn">Sửa</button>
                    <button type="button" class="delete-btn">Xóa</button>
                </div>
            </form>

            <div class="search-container">
                <input type="text" class="search-input" placeholder="Tìm theo mã sinh viên hoặc tên môn học...">
            </div>
            <div class="grades-table">
                <table>
                    <thead>
                        <tr>
                            <th style="display: none;">Mã điểm</th>
                            <th>Mã sinh viên</th>
                            <th>Tên môn học</th>
                            <th>Điểm quá trình</th>
                            <th>Điểm chuyên cần</th>
                            <th>Điểm giữa kỳ</th>
                            <th>Điểm cuối kỳ</th>
                            <th>Tổng điểm</th>
                            <th>Điểm chữ</th>
                            <th>Ghi chú</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                            List<Grade> grades = (List<Grade>) request.getAttribute("grades");
                            if (grades != null && !grades.isEmpty()) {
                                for (Grade grade : grades) {
                        %>
                            <tr>
                                <td style="display: none;"><%= grade.getGradeId() %></td>
                                <td><%= grade.getStudentId() %></td>
                                <td><%= grade.getSubjectName() != null ? grade.getSubjectName() : "N/A" %></td>
                                <td><%= grade.getScore() %></td>
                                <td><%= grade.getAttendance() %></td>
                                <td><%= grade.getMidterm() %></td>
                                <td><%= grade.getFinalExam() %></td>
                                <td><%= String.format("%.1f", grade.getTotal()) %></td>
                                <td><%= grade.getLetterGrade() != null ? grade.getLetterGrade() : "N/A" %></td>
                                <td><%= grade.getNote() != null ? grade.getNote() : "N/A" %></td>
                            </tr>
                        <% 
                                }
                            } else {
                        %>
                            <tr><td colspan="9">Không có điểm nào trong lớp này.</td></tr>
                        <% } %>
                    </tbody>
                </table>
            </div>

            <div class="back-button-container">
                <a href="home.jsp"><button class="back-button">Quay lại</button></a>
            </div>
        <% } %>
    </main>

    <footer class="footer">
        <p>© 2025 Trường Đại Học Công Nghệ Đông Á</p>
    </footer>
</body>
</html>