package servlet;

import dao.ClassDAO;
import dao.ReportDao;
import dao.SubjectDao;
import dao.CourseDao;
import model.Account;
import model.Report;
import model.Subject;
import model.ql_class;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/ReportServlet")
public class ReportServlet extends HttpServlet {
    private ReportDao reportDao;
    private ClassDAO classDao;
    private SubjectDao subjectDao;
    private CourseDao courseDao;

    @Override
    public void init() throws ServletException {
        reportDao = new ReportDao();
        classDao = new ClassDAO();
        subjectDao = new SubjectDao();
        courseDao = new CourseDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");

        if (account == null) {
            System.out.println("Chưa đăng nhập, chuyển hướng về login.jsp.");
            response.sendRedirect("login.jsp");
            return;
        }

        String classId = request.getParameter("class_id");
        String subjectId = request.getParameter("subject_id");
        String major = request.getParameter("major");
        String block = request.getParameter("block");

        List<Report> reports = new ArrayList<>();
        List<ql_class> classes = new ArrayList<>();
        List<Subject> subjects = new ArrayList<>();
        List<String> majors = new ArrayList<>();
        String[] blocksArray = null;

        try {
            reports = reportDao.getReports(account, classId, subjectId);
            request.setAttribute("reports", reports);

            try {
                classes = reportDao.getAllClasses(account);
                request.setAttribute("classes", classes);
                System.out.println("Số lớp học tìm thấy: " + (classes != null ? classes.size() : 0));
            } catch (Exception e) {
                System.err.println("Lỗi khi lấy danh sách lớp học: " + e.getMessage());
                request.setAttribute("errorMessage", "Lỗi khi lấy danh sách lớp học: " + e.getMessage());
            }

            try {
                subjects = reportDao.getAllSubjects();
                request.setAttribute("subjects", subjects);
                System.out.println("Số môn học tìm thấy: " + (subjects != null ? subjects.size() : 0));
            } catch (Exception e) {
                System.err.println("Lỗi khi lấy danh sách môn học: " + e.getMessage());
                request.setAttribute("errorMessage", "Lỗi khi lấy danh sách môn học: " + e.getMessage());
            }

            try {
                majors = courseDao.getAllCourseNames(); // Sử dụng getAllCourseNames() thay vì getAllCourses()
                request.setAttribute("majors", majors.toArray(new String[0])); // Chuyển List<String> thành String[]
                System.out.println("Số ngành học tìm thấy: " + (majors != null ? majors.size() : 0));
            } catch (Exception e) {
                System.err.println("Lỗi khi lấy danh sách ngành học: " + e.getMessage());
                request.setAttribute("errorMessage", "Lỗi khi lấy danh sách ngành học: " + e.getMessage());
            }

            // Giả lập danh sách khối học (cần thay bằng dữ liệu thực từ database)
            String[] blocks = {"K21", "K22"};
            blocksArray = blocks;
            request.setAttribute("blocks", blocksArray);

            request.setAttribute("selectedClassId", classId);
            request.setAttribute("selectedSubjectId", subjectId);
            request.setAttribute("selectedMajor", major);
            request.setAttribute("selectedBlock", block);

            System.out.println("Số báo cáo tìm thấy: " + (reports != null ? reports.size() : 0));
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy dữ liệu báo cáo: " + e.getMessage());
            request.setAttribute("errorMessage", "Lỗi khi lấy dữ liệu báo cáo: " + e.getMessage());
        }

        request.getRequestDispatcher("report.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");

        if (account == null) {
            System.out.println("Chưa đăng nhập, chuyển hướng về login.jsp.");
            response.sendRedirect("login.jsp");
            return;
        }

        String classId = request.getParameter("class_id");
        String subjectId = request.getParameter("subject_id");
        String major = request.getParameter("major");
        String block = request.getParameter("block");

        StringBuilder redirectUrl = new StringBuilder("ReportServlet");
        boolean hasParam = false;

        if (classId != null && !classId.isEmpty()) {
            redirectUrl.append("?class_id=").append(classId);
            hasParam = true;
        }
        if (subjectId != null && !subjectId.isEmpty()) {
            redirectUrl.append(hasParam ? "&" : "?").append("subject_id=").append(subjectId);
            hasParam = true;
        }
        if (major != null && !major.isEmpty()) {
            redirectUrl.append(hasParam ? "&" : "?").append("major=").append(major);
            hasParam = true;
        }
        if (block != null && !block.isEmpty()) {
            redirectUrl.append(hasParam ? "&" : "?").append("block=").append(block);
        }

        response.sendRedirect(redirectUrl.toString());
    }
}