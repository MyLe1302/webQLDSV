<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Report" %>
<%@ page import="model.ql_class" %>
<%@ page import="model.Subject" %>
<%@ page import="model.Account" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Báo Cáo Thống Kê Điểm Sinh Viên</title>
    <link rel="stylesheet" href="css/style.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
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
        .form-container select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 0.9rem;
            box-sizing: border-box;
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
        .action-buttons .filter-btn {
            background-color: #0056b3;
            color: white;
        }
        .action-buttons .filter-btn:hover {
            background-color: #003d82;
        }
        .reports-table {
            margin: 20px 0;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            overflow-x: auto;
        }
        table {
            min-width: 400px;
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
        tr:hover {
            background-color: #f5f5f5;
        }
        .chart-container {
            margin: 20px 0;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
            max-width: 600px;
        }
        .back-button-container {
            margin-top: 20px;
            margin-bottom: 20px;
            display: flex;
        }
        .error {
            color: red;
            font-weight: bold;
        }
    </style>
    <script>
        document.addEventListener("DOMContentLoaded", function() {
            document.querySelector(".filter-btn").addEventListener("click", function() {
                document.querySelector(".form-container").submit();
            });

            <% 
                List<Report> reports = (List<Report>) request.getAttribute("reports");
                int totalGradeA = 0, totalGradeB = 0, totalGradeC = 0, totalGradeD = 0;
                if (reports != null && !reports.isEmpty()) {
                    for (Report report : reports) {
                        totalGradeA += report.getCountGradeA();
                        totalGradeB += report.getCountGradeB();
                        totalGradeC += report.getCountGradeC();
                        totalGradeD += report.getCountGradeD();
                    }
                }
            %>
            // Tạo biểu đồ
            const ctx = document.getElementById('gradeChart').getContext('2d');
            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: ['Điểm A', 'Điểm B', 'Điểm C', 'Điểm D'],
                    datasets: [{
                        label: 'Số lượng sinh viên',
                        data: [<%= totalGradeA %>, <%= totalGradeB %>, <%= totalGradeC %>, <%= totalGradeD %>],
                        backgroundColor: [
                            'rgba(54, 162, 235, 0.6)',
                            'rgba(255, 206, 86, 0.6)',
                            'rgba(75, 192, 192, 0.6)',
                            'rgba(255, 99, 132, 0.6)'
                        ],
                        borderColor: [
                            'rgba(54, 162, 235, 1)',
                            'rgba(255, 206, 86, 1)',
                            'rgba(75, 192, 192, 1)',
                            'rgba(255, 99, 132, 1)'
                        ],
                        borderWidth: 1
                    }]
                },
                options: {
                    scales: {
                        y: {
                            beginAtZero: true,
                            title: {
                                display: true,
                                text: 'Số lượng sinh viên'
                            }
                        },
                        x: {
                            title: {
                                display: true,
                                text: 'Mức điểm'
                            }
                        }
                    },
                    plugins: {
                        legend: {
                            display: false
                        }
                    }
                }
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
        if (!"admin".equals(currentUser.getRole()) && !"teacher".equals(currentUser.getRole())) {
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
            <a href="ClassServlet">Lớp Học</a>
            <a href="ReportServlet" class="active">Báo cáo</a>
            <a href="logout" class="logout">Đăng Xuất</a>
        </div>
    </nav>

    <main class="main">
        <h2 class="welcome">Báo Cáo Thống Kê Điểm Sinh Viên</h2>
        <% if (request.getAttribute("errorMessage") != null) { %>
            <p class="error"><%= request.getAttribute("errorMessage") %></p>
        <% } %>

        <!-- Form lọc báo cáo -->
        <form action="ReportServlet" method="post" class="form-container">
            <div>
                <label>Chọn ngành học:</label>
            <select name="major">
                <option value="">Tất cả ngành học</option>
                <% 
                    String[] majors = (String[]) request.getAttribute("majors");
                    String selectedMajor = (String) request.getAttribute("selectedMajor");
                    if (majors != null) {
                        for (String major : majors) { 
                %>
                    <option value="<%= major %>" <%= major.equals(selectedMajor) ? "selected" : "" %>><%= major %></option>
                <% 
                        }
                    }
                %>
            </select>
            </div>
            <div>
                <label>Chọn lớp học:</label>
                <select name="class_id">
                    <option value="">Tất cả lớp học</option>
                    <% 
                        List<ql_class> classes = (List<ql_class>) request.getAttribute("classes");
                        String selectedClassId = (String) request.getAttribute("selectedClassId");
                        if (classes != null && !classes.isEmpty()) {
                            for (ql_class classObj : classes) { 
                    %>
                        <option value="<%= classObj.getClassId() %>" <%= classObj.getClassId().equals(selectedClassId) ? "selected" : "" %>>
                            <%= classObj.getClassName() != null ? classObj.getClassName() : classObj.getClassId() %>
                        </option>
                    <% 
                            }
                        }
                    %>
                </select>
            </div>
            <div>
                <label>Chọn môn học:</label>
                <select name="subject_id">
                    <option value="">Tất cả môn học</option>
                    <% 
                        List<Subject> subjects = (List<Subject>) request.getAttribute("subjects");
                        String selectedSubjectId = (String) request.getAttribute("selectedSubjectId");
                        if (subjects != null && !subjects.isEmpty()) {
                            for (Subject subject : subjects) { 
                    %>
                        <option value="<%= subject.getSubjectId() %>" <%= subject.getSubjectId().equals(selectedSubjectId) ? "selected" : "" %>>
                            <%= subject.getSubjectName() != null ? subject.getSubjectName() : subject.getSubjectId() %>
                        </option>
                    <% 
                            }
                        }
                    %>
                </select>
            </div>
            <div class="action-buttons">
                <button type="button" class="filter-btn">Lọc</button>
            </div>
        </form>

        <!-- Danh sách báo cáo -->
        <div class="reports-table">
            <table>
                <thead>
                    <tr>
                        <th>Điểm A</th>
                        <th>Điểm B</th>
                        <th>Điểm C</th>
                        <th>Điểm D</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        if (reports != null && !reports.isEmpty()) {
                    %>
                        <tr>
                            <td><%= totalGradeA %></td>
                            <td><%= totalGradeB %></td>
                            <td><%= totalGradeC %></td>
                            <td><%= totalGradeD %></td>
                        </tr>
                    <% 
                        } else {
                    %>
                        <tr><td colspan="4">Không có dữ liệu báo cáo.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>

        <!-- Biểu đồ -->
        <div class="chart-container">
            <canvas id="gradeChart"></canvas>
        </div>

        <!-- Nút Quay lại -->
        <div class="back-button-container">
            <a href="home.jsp"><button class="back-button">Quay lại</button></a>
        </div>
    </main>

    <footer class="footer">
        <p>© 2025 Trường Đại Học Công Nghệ ĐÔNG Á</p>
    </footer>
</body>
</html>