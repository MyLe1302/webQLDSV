package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Account;
import model.Subject;
import dao.SubjectDao;

@WebServlet("/SubjectServlet")
public class SubjectServlet extends HttpServlet {
    private SubjectDao subjectDao;

    @Override
    public void init() throws ServletException {
        subjectDao = new SubjectDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            List<Subject> subjects;
            String role = account.getRole();
            if ("admin".equalsIgnoreCase(role)) {
                subjects = subjectDao.getAllSubjects();
            } else if ("teacher".equalsIgnoreCase(role) || "student".equalsIgnoreCase(role)) {
                subjects = subjectDao.getSubjectsByAccount(account.getAccountId(), role);
            } else {
                request.setAttribute("errorMessage", "Bạn không có quyền truy cập trang này!");
                request.getRequestDispatcher("home.jsp").forward(request, response);
                return;
            }
            request.setAttribute("subjects", subjects);
        } catch (SQLException | ClassNotFoundException e) {
            request.setAttribute("errorMessage", "Lỗi khi lấy danh sách môn học: " + e.getMessage());
            System.err.println("Lỗi: " + e.getMessage());
        }

        request.getRequestDispatcher("manage_subjects.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Chỉ admin được thực hiện hành động POST
        if (!"admin".equalsIgnoreCase(account.getRole())) {
            request.setAttribute("errorMessage", "Bạn không có quyền thực hiện hành động này!");
            doGet(request, response);
            return;
        }

        String action = request.getParameter("action");
        String subjectId = request.getParameter("subject_id");
        String subjectName = request.getParameter("subject_name");
        String creditsStr = request.getParameter("credits");

        try {
            Integer credits = creditsStr != null && !creditsStr.trim().isEmpty() ? Integer.parseInt(creditsStr) : 0;

            if ("add".equalsIgnoreCase(action)) {
                if (subjectId == null || subjectId.trim().isEmpty() || subjectName == null || subjectName.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng nhập đầy đủ mã và tên môn học!");
                    request.setAttribute("subject_id", subjectId);
                    request.setAttribute("subject_name", subjectName);
                    request.setAttribute("credits", creditsStr);
                } else if (subjectDao.getSubjectById(subjectId) != null) {
                    request.setAttribute("errorMessage", "Mã môn học đã tồn tại!");
                    request.setAttribute("subject_id", subjectId);
                    request.setAttribute("subject_name", subjectName);
                    request.setAttribute("credits", creditsStr);
                } else {
                    Subject newSubject = new Subject(subjectId.trim(), subjectName.trim(), credits);
                    subjectDao.addSubject(newSubject);
                    request.setAttribute("successMessage", "Thêm môn học thành công!");
                }
            } else if ("update".equalsIgnoreCase(action)) {
                if (subjectId == null || subjectId.trim().isEmpty() || subjectName == null || subjectName.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng nhập đầy đủ mã và tên môn học!");
                } else if (subjectDao.getSubjectById(subjectId) == null) {
                    request.setAttribute("errorMessage", "Mã môn học không tồn tại!");
                } else {
                    Subject updatedSubject = new Subject(subjectId.trim(), subjectName.trim(), credits);
                    subjectDao.updateSubject(updatedSubject);
                    request.setAttribute("successMessage", "Cập nhật môn học thành công!");
                }
            } else if ("delete".equalsIgnoreCase(action)) {
                if (subjectId == null || subjectId.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng chọn môn học để xóa!");
                } else if (subjectDao.getSubjectById(subjectId) == null) {
                    request.setAttribute("errorMessage", "Mã môn học không tồn tại!");
                } else {
                    subjectDao.deleteSubject(subjectId.trim());
                    request.setAttribute("successMessage", "Xóa môn học thành công!");
                }
            } else {
                request.setAttribute("errorMessage", "Hành động không hợp lệ!");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Số tín chỉ phải là một số hợp lệ!");
        } catch (SQLException | ClassNotFoundException e) {
            String errorMessage = ((SQLException) e).getSQLState() != null && ((SQLException) e).getSQLState().startsWith("23")
                ? "Lỗi: Môn học đang được sử dụng và không thể thực hiện hành động."
                : "Lỗi: " + e.getMessage();
            request.setAttribute("errorMessage", errorMessage);
            System.err.println("Lỗi: " + e.getMessage());
        }

        // Lấy lại danh sách môn học
        try {
            List<Subject> subjects = subjectDao.getAllSubjects();
            request.setAttribute("subjects", subjects);
        } catch (SQLException | ClassNotFoundException e) {
            request.setAttribute("errorMessage", "Lỗi khi lấy danh sách môn học: " + e.getMessage());
            System.err.println("Lỗi: " + e.getMessage());
        }

        request.getRequestDispatcher("manage_subjects.jsp").forward(request, response);
    }
}