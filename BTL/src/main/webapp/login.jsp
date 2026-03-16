<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        html, body {
            margin: 0;
            padding: 0;
            height: 100%; /* Đảm bảo html và body chiếm toàn bộ chiều cao */
        }

        body {
            font-family: Arial, sans-serif;
            background-color: #f0f2f5; /* Màu nền nhạt giống hình */
            min-height: 100vh; /* Đảm bảo body chiếm toàn bộ chiều cao */
        }

        .navbar {
            background-color: #003087; /* Màu xanh đậm giống hình */
            color: white;
            padding: 0.5rem 1rem;
            display: flex;
            justify-content: center;
            align-items: center;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            z-index: 1000;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }

        .navbar-left {
            display: flex;
            align-items: center;
        }

        .logo {
            width: 40px;
            height: 40px;
            margin-right: 0.5rem;
            background-color: #fff;
            border-radius: 50%;
        }

        .navbar-left h1 {
            font-size: 1.1rem;
            margin: 0;
            font-weight: bold;
            text-transform: uppercase;
        }

        .login-container {
            background-color: white;
            padding: 2rem;
            border-radius: 0.5rem; /* Bo góc giống hình */
            width: 100%;
            max-width: 350px; /* Kích thước giống hình */
            text-align: center;
            position: auto; /* Cố định ở giữa màn hình */
            
            z-index: 1000; /* Đảm bảo hiển thị trên các phần tử khác */
        }

        .login-title {
            font-size: 1.5rem;
            font-weight: bold;
            margin-bottom: 1.5rem;
            color: #333;
        }

        .error-message {
            color: #dc3545;
            margin-bottom: 1rem;
            font-size: 0.9rem;
        }

        .form-group {
            margin-bottom: 1.5rem;
            text-align: left;
        }

        .form-group label {
            display: block;
            font-size: 0.9rem;
            font-weight: 500;
            color: #666;
            margin-bottom: 0.5rem;
        }

        .form-group input {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #ccc;
            border-radius: 0.25rem;
            font-size: 1rem;
            box-sizing: border-box;
        }

        button {
            width: 100%;
            padding: 0.75rem;
            background-color: #007bff; /* Màu xanh giống nút trong hình */
            color: white;
            border: none;
            border-radius: 0.25rem;
            cursor: pointer;
            font-size: 1rem;
            font-weight: 500;
        }

        button:hover {
            background-color: #0056b3; /* Màu xanh đậm hơn khi hover */
        }

        .footer {
            background-color: #003087; /* Màu xanh đậm giống navbar */
            color: white;
            text-align: center;
            padding: 0.5rem 0;
            width: 100%;
            position: fixed; /* Ghim cố định footer */
            bottom: 0;
            left: 0;
            z-index: 1000; /* Đảm bảo footer hiển thị trên nội dung */
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="navbar-left">
            <img src="images/Logo.png" alt="Logo" class="logo">
            <h1>TRƯỜNG ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á</h1>
        </div>
    </nav>
    <div class="login-container">
        <h2 class="login-title">Đăng nhập</h2>
        <% if (request.getAttribute("error") != null) { %>
            <p class="error-message"><%= request.getAttribute("error") %></p>
        <% } %>
        <form action="login" method="post">
            <div class="form-group">
                <label for="username">Tên người dùng</label>
                <input type="text" id="username" name="username" required>
            </div>
            <div class="form-group">
                <label for="password">Nhập mật khẩu</label>
                <input type="password" id="password" name="password" required>
            </div>
            <button type="submit">Đăng nhập</button>
        </form>
    </div>
    
    <footer class="footer">
        <p>© 2025 Trường Đại Học Công Nghệ Đông Á</p>
    </footer>
    
</body>
</html>