package servlet;

import dao.AccountDao;
import model.Account;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/AccountServlet")
public class AccountServlet extends HttpServlet {
    private AccountDao accountDao;

    @Override
    public void init() throws ServletException {
        accountDao = new AccountDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Account> accounts = accountDao.getAllAccounts();
            request.setAttribute("accounts", accounts);
            request.getRequestDispatcher("account.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Lỗi khi xử lý yêu cầu: " + e.getMessage());
            request.setAttribute("errorMessage", "Lỗi khi xử lý yêu cầu: " + e.getMessage());
            request.getRequestDispatcher("account.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String accountIdStr = request.getParameter("account_id");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String role = request.getParameter("role");
        boolean approved = "true".equals(request.getParameter("approved"));

        // Kiểm tra các trường bắt buộc
        StringBuilder errorMsg = new StringBuilder();
        if (username == null || username.trim().isEmpty()) {
            errorMsg.append("Tên người dùng không được để trống. ");
        }
        if (password == null || password.trim().isEmpty()) {
            errorMsg.append("Mật khẩu không được để trống. ");
        }
        if (email == null || email.trim().isEmpty()) {
            errorMsg.append("Email không được để trống. ");
        }
        if (role == null || role.trim().isEmpty()) {
            errorMsg.append("Vui lòng chọn vai trò. ");
        }

        if (errorMsg.length() > 0) {
            request.setAttribute("errorMessage", errorMsg.toString());
            doGet(request, response);
            return;
        }

        try {
            int accountId = accountIdStr != null && !accountIdStr.isEmpty() ? Integer.parseInt(accountIdStr) : 0;
            Account account = new Account(accountId, username, password, email, role, approved, null);

            if ("add".equals(action)) {
                if (accountDao.isEmailExists(email)) {
                    request.setAttribute("errorMessage", "Email đã tồn tại: " + email);
                    doGet(request, response);
                    return;
                }
                if (accountDao.getAccountByUsername(username) != null) {
                    request.setAttribute("errorMessage", "Tên người dùng đã tồn tại: " + username);
                    doGet(request, response);
                    return;
                }
                account.setAccountId(0); // Để database tự sinh ID
                accountDao.addAccount(account);
                request.setAttribute("successMessage", "Thêm tài khoản thành công!");
            } else if ("update".equals(action)) {
                if (accountId == 0 || !accountDao.isAccountExists(accountId)) {
                    request.setAttribute("errorMessage", "ID tài khoản không hợp lệ!");
                } else {
                    Account existingAccount = accountDao.getAccountById(accountId);
                    if (!email.equals(existingAccount.getEmail()) && accountDao.isEmailExists(email)) {
                        request.setAttribute("errorMessage", "Email đã tồn tại: " + email);
                        doGet(request, response);
                        return;
                    }
                    if (!username.equals(existingAccount.getUsername()) && accountDao.getAccountByUsername(username) != null) {
                        request.setAttribute("errorMessage", "Tên người dùng đã tồn tại: " + username);
                        doGet(request, response);
                        return;
                    }
                    accountDao.updateAccount(account);
                    request.setAttribute("successMessage", "Cập nhật tài khoản thành công!");
                }
            } else if ("delete".equals(action)) {
                if (accountId == 0 || !accountDao.isAccountExists(accountId)) {
                    request.setAttribute("errorMessage", "ID tài khoản không hợp lệ!");
                } else {
                    accountDao.deleteAccount(accountId);
                    request.setAttribute("successMessage", "Xóa tài khoản thành công!");
                }
            } else {
                request.setAttribute("errorMessage", "Hành động không hợp lệ!");
            }
            doGet(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Lỗi khi xử lý: " + e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                request.setAttribute("errorMessage", "Email hoặc username đã tồn tại!");
            } else {
                request.setAttribute("errorMessage", "Lỗi khi xử lý: " + e.getMessage());
            }
            doGet(request, response);
        } catch (NumberFormatException e) {
            System.err.println("Lỗi định dạng accountId: " + e.getMessage());
            request.setAttribute("errorMessage", "ID không hợp lệ!");
            doGet(request, response);
        }
    }
}