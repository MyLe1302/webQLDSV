package servlet;

import model.Account;
import dao.AccountDao;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private AccountDao accountDao;

    @Override
    public void init() {
        accountDao = new AccountDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("account") != null) {
            response.sendRedirect("home.jsp");
        } else {
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println("Dữ liệu từ form: username=" + username + ", password=" + password);

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng điền đầy đủ thông tin!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        try {
            Account account = accountDao.authenticate(username, password);
            if (account != null) {
                HttpSession session = request.getSession();
                session.setAttribute("account", account);
                System.out.println("Đăng nhập thành công: " + account.getUsername());
                response.sendRedirect("home.jsp");
            } else {
                System.out.println("Đăng nhập thất bại cho username: " + username);
                request.setAttribute("error", "Tên người dùng hoặc mật khẩu không đúng, hoặc tài khoản chưa được phê duyệt!");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Lỗi cơ sở dữ liệu khi đăng nhập: " + e.getMessage());
            request.setAttribute("error", "Lỗi hệ thống, vui lòng thử lại sau!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}