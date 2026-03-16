package servlet;

import dao.ClassDAO;
import dao.SubjectDao;
import dao.TeacherDAO;
import model.Account;
import model.ql_class;
import model.Subject;
import model.Teacher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

@WebServlet("/ClassServlet")
public class ClassServlet extends HttpServlet {
    private ClassDAO classDAO;
    private SubjectDao subjectDao;
    private TeacherDAO teacherDAO;

    @Override
    public void init() throws ServletException {
        classDAO = new ClassDAO();
        subjectDao = new SubjectDao();
        teacherDAO = new TeacherDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");
        String action = request.getParameter("action");

        try {
            // Tải danh sách môn học và giảng viên
            List<Subject> subjects = subjectDao.getAllSubjects();
            List<Teacher> teachers = teacherDAO.getAllTeachers();
            request.setAttribute("subjects", subjects);
            request.setAttribute("teachers", teachers);

            // Tải danh sách lớp học
            List<ql_class> classes = loadClasses(account);
            request.setAttribute("classes", classes);

            if (action == null || action.isEmpty()) {
                request.getRequestDispatcher("manage_classes.jsp").forward(request, response);
            } else if (action.equals("delete")) {
                String classId = request.getParameter("class_id");
                if (classId == null || classId.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Mã lớp không hợp lệ!");
                } else {
                    try {
                        classDAO.deleteClass(classId);
                        request.setAttribute("successMessage", "Xóa lớp học thành công!");
                    } catch (SQLException | IllegalArgumentException e) {
                        request.setAttribute("errorMessage", "Lỗi khi xóa lớp học: " + e.getMessage());
                    }
                }
                // Tải lại danh sách lớp học sau khi xóa
                classes = loadClasses(account);
                request.setAttribute("classes", classes);
                request.getRequestDispatcher("manage_classes.jsp").forward(request, response);
            } else if (action.equals("get_subjects_for_class")) {
                String classId = request.getParameter("class_id");
                if (classId == null || classId.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Mã lớp không hợp lệ\"}");
                    return;
                }
                try {
                    List<Subject> classSubjects = classDAO.getSubjectsForClass(classId);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter out = response.getWriter();
                    out.write(new Gson().toJson(classSubjects));
                    out.flush();
                } catch (SQLException | ClassNotFoundException e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"error\": \"Lỗi khi lấy danh sách môn học: " + e.getMessage() + "\"}");
                }
                return;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi truy xuất dữ liệu: " + e.getMessage());
            // Tải lại danh sách lớp học ngay cả khi có lỗi
            List<ql_class> classes = loadClasses(account);
            request.setAttribute("classes", classes);
            request.getRequestDispatcher("manage_classes.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");

        try {
            // Tải danh sách môn học và giảng viên
            List<Subject> subjects = subjectDao.getAllSubjects();
            List<Teacher> teachers = teacherDAO.getAllTeachers();
            request.setAttribute("subjects", subjects);
            request.setAttribute("teachers", teachers);

            if (action.equals("delete")) {
                String classId = request.getParameter("class_id");
                if (classId == null || classId.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Mã lớp không hợp lệ!");
                } else {
                    try {
                        classDAO.deleteClass(classId);
                        request.setAttribute("successMessage", "Xóa lớp học thành công!");
                    } catch (SQLException | IllegalArgumentException e) {
                        request.setAttribute("errorMessage", "Lỗi khi xóa lớp học: " + e.getMessage());
                    }
                }
            } else if (action.equals("assign_subject")) {
                String classId = request.getParameter("class_id");
                String subjectId = request.getParameter("subject_id");

                if (classId == null || classId.trim().isEmpty() || subjectId == null || subjectId.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng chọn lớp học và môn học.");
                    request.setAttribute("selectedClassId", classId);
                    request.setAttribute("selectedSubjectId", subjectId);
                } else {
                    try {
                        classDAO.assignSubjectToClass(classId, subjectId);
                        request.setAttribute("successMessage", "Thêm môn học vào lớp thành công!");
                    } catch (SQLException e) {
                        request.setAttribute("errorMessage", "Lỗi khi thêm môn học: " + e.getMessage());
                        request.setAttribute("selectedClassId", classId);
                        request.setAttribute("selectedSubjectId", subjectId);
                    }
                }
            } else {
                String classId = request.getParameter("class_id");
                String className = request.getParameter("class_name");
                String subjectId = request.getParameter("subject_id");
                String teacherId = request.getParameter("teacher_id");

                if (className == null || className.trim().isEmpty() || subjectId == null || subjectId.trim().isEmpty()) {
                    throw new IllegalArgumentException("Tên lớp và môn học chính là bắt buộc!");
                }

                ql_class classObj = new ql_class(classId, className, subjectId, null, teacherId, null);

                if (action.equals("add")) {
                    classDAO.addClass(classObj);
                    request.setAttribute("successMessage", "Thêm lớp học thành công!");
                } else if (action.equals("update")) {
                    if (classId == null || classId.trim().isEmpty()) {
                        throw new IllegalArgumentException("Mã lớp không hợp lệ để cập nhật!");
                    }
                    classDAO.updateClass(classObj);
                    request.setAttribute("successMessage", "Cập nhật lớp học thành công!");
                }
            }

            // Tải lại danh sách lớp học
            List<ql_class> classes = loadClasses(account);
            request.setAttribute("classes", classes);
            request.getRequestDispatcher("manage_classes.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException | IllegalArgumentException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi lưu dữ liệu: " + e.getMessage());
            // Tải lại danh sách lớp học ngay cả khi có lỗi
            List<ql_class> classes = loadClasses(account);
            request.setAttribute("classes", classes);
            request.getRequestDispatcher("manage_classes.jsp").forward(request, response);
        }
    }

    // Phương thức phụ để tải danh sách lớp học với xử lý lỗi
    private List<ql_class> loadClasses(Account account) {
        List<ql_class> classes = new ArrayList<>();
        try {
            if (account != null && account.getRole() != null && ("admin".equals(account.getRole()) || "teacher".equals(account.getRole()) || "student".equals(account.getRole()))) {
                classes = classDAO.getAllClasses(account);
            } else {
                classes = classDAO.getAllClasses();
            }
        } catch (SQLException | ClassNotFoundException | IllegalArgumentException e) {
            e.printStackTrace();
            // Trả về danh sách rỗng nếu có lỗi
        }
        return classes;
    }
}