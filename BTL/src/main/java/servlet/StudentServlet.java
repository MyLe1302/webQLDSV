package servlet;

import dao.StudentDao;
import dao.ClassDAO;
import dao.AccountDao;
import model.Student;
import model.Account;
import model.ql_class;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/StudentServlet")
public class StudentServlet extends HttpServlet {
    private StudentDao studentDAO;
    private ClassDAO classDAO;
    private AccountDao accountDAO;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDao();
        classDAO = new ClassDAO();
        accountDAO = new AccountDao();
    }

    // Hàm hỗ trợ để lấy tất cả className theo classId
    private Map<String, String> getClassNamesMap() throws SQLException, ClassNotFoundException {
        Map<String, String> classNamesMap = new HashMap<>();
        List<ql_class> classes = classDAO.getAllClasses();
        if (classes != null) {
            for (ql_class cls : classes) {
                if (cls != null) {
                    String classId = cls.getClassId();
                    String className = cls.getClassName();
                    if (classId != null && className != null) {
                        classNamesMap.put(classId, className);
                    }
                }
            }
        }
        return classNamesMap;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Student> students = new ArrayList<>();
        List<ql_class> classes = new ArrayList<>();

        try {
            students = studentDAO.getAllStudents();
            classes = classDAO.getAllClasses();
            // Lấy map classId -> className
            Map<String, String> classNamesMap = getClassNamesMap();
            // Gán className cho mỗi sinh viên
            for (Student student : students) {
                String className = classNamesMap.getOrDefault(String.valueOf(student.getClassId()), "");
                student.setClassName(className);
            }
            request.setAttribute("students", students);
            request.setAttribute("classes", classes);
            request.getRequestDispatcher("manage_students.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            request.setAttribute("errorMessage", "Lỗi khi truy xuất dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("manage_students.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        List<Student> students = new ArrayList<>();
        List<ql_class> classes = new ArrayList<>();

        try {
            students = studentDAO.getAllStudents();
            classes = classDAO.getAllClasses();
            // Lấy map classId -> className
            Map<String, String> classNamesMap = getClassNamesMap();
            // Gán className cho mỗi sinh viên
            for (Student student : students) {
                String className = classNamesMap.getOrDefault(String.valueOf(student.getClassId()), "");
                student.setClassName(className);
            }
            request.setAttribute("students", students);
            request.setAttribute("classes", classes);

            if (action == null || action.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Hành động không hợp lệ.");
                request.getRequestDispatcher("manage_students.jsp").forward(request, response);
                return;
            }

            if (action.equals("delete")) {
                String studentId = request.getParameter("student_id");
                if (studentId == null || studentId.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Mã sinh viên không hợp lệ.");
                    request.getRequestDispatcher("manage_students.jsp").forward(request, response);
                    return;
                }
                Student existingStudent = studentDAO.getStudentById(studentId);
                if (existingStudent == null) {
                    request.setAttribute("errorMessage", "Không tìm thấy sinh viên với mã: " + studentId);
                    request.getRequestDispatcher("manage_students.jsp").forward(request, response);
                    return;
                }
                studentDAO.deleteStudent(studentId);
                request.setAttribute("successMessage", "Xóa sinh viên thành công!");
                students = studentDAO.getAllStudents();
                // Gán className cho danh sách sinh viên mới
                for (Student student : students) {
                    String className = classNamesMap.getOrDefault(String.valueOf(student.getClassId()), "");
                    student.setClassName(className);
                }
                request.setAttribute("students", students);
                request.getRequestDispatcher("manage_students.jsp").forward(request, response);
                return;
            }

            String studentId = request.getParameter("student_id");
            String name = request.getParameter("name");
            String department = request.getParameter("department");
            String birthDateStr = request.getParameter("birth_date");
            String gender = request.getParameter("gender");
            String classIdStr = request.getParameter("class_id");

            if (studentId == null || studentId.trim().isEmpty() || name == null || name.trim().isEmpty() ||
                department == null || department.trim().isEmpty() || birthDateStr == null || birthDateStr.trim().isEmpty() ||
                gender == null || !gender.matches("Nam|Nữ") || classIdStr == null || classIdStr.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng điền đầy đủ và đúng định dạng các trường.");
                request.getRequestDispatcher("manage_students.jsp").forward(request, response);
                return;
            }

            int classId;
            try {
                classId = Integer.parseInt(classIdStr);
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Mã lớp không hợp lệ.");
                request.getRequestDispatcher("manage_students.jsp").forward(request, response);
                return;
            }

            Date birthDate;
            try {
                birthDate = Date.valueOf(birthDateStr);
            } catch (IllegalArgumentException e) {
                request.setAttribute("errorMessage", "Ngày sinh không hợp lệ. Định dạng phải là yyyy-MM-dd.");
                request.getRequestDispatcher("manage_students.jsp").forward(request, response);
                return;
            }

            Student student = new Student();
            student.setStudentId(studentId);
            student.setName(name);
            student.setDepartment(department);
            student.setBirthDate(birthDate);
            student.setGender(gender);
            student.setClassId(classId);
            student.setCourseId(1); // Giá trị mặc định
            student.setPhoto(null); // Giá trị mặc định

            if (action.equals("add")) {
                Student existingStudent = studentDAO.getStudentById(studentId);
                if (existingStudent != null) {
                    request.setAttribute("errorMessage", "Mã sinh viên đã tồn tại: " + studentId);
                    request.getRequestDispatcher("manage_students.jsp").forward(request, response);
                    return;
                }

                String username = "student_" + studentId;
                String email = studentId + "@example.com";
                String password = "defaultPassword"; // Nên mã hóa mật khẩu trong thực tế
                Account account = new Account();
                account.setUsername(username);
                account.setEmail(email);
                account.setPassword(password);
                account.setRole("student");
                account.setApproved(false);
                int accountId = accountDAO.addAccount(account);
                if (accountId == -1) {
                    request.setAttribute("errorMessage", "Không thể tạo tài khoản cho sinh viên.");
                    request.getRequestDispatcher("manage_students.jsp").forward(request, response);
                    return;
                }

                student.setAccountId(accountId);
                studentDAO.addStudent(student);
                request.setAttribute("successMessage", "Thêm sinh viên thành công!");
                students = studentDAO.getAllStudents();
                // Gán className cho danh sách sinh viên mới
                for (Student studentItem : students) {
                    String className = classNamesMap.getOrDefault(String.valueOf(studentItem.getClassId()), "");
                    studentItem.setClassName(className);
                }
                request.setAttribute("students", students);
            } else if (action.equals("update")) {
                Student existingStudent = studentDAO.getStudentById(studentId);
                if (existingStudent == null) {
                    request.setAttribute("errorMessage", "Không tìm thấy sinh viên để sửa: " + studentId);
                    request.getRequestDispatcher("manage_students.jsp").forward(request, response);
                    return;
                }
                student.setCourseId(existingStudent.getCourseId());
                student.setAccountId(existingStudent.getAccountId());
                student.setPhoto(existingStudent.getPhoto());
                studentDAO.updateStudent(student);
                request.setAttribute("successMessage", "Cập nhật sinh viên thành công!");
                students = studentDAO.getAllStudents();
                // Gán className cho danh sách sinh viên mới
                for (Student studentItem : students) {
                    String className = classNamesMap.getOrDefault(String.valueOf(studentItem.getClassId()), "");
                    studentItem.setClassName(className);
                }
                request.setAttribute("students", students);
            }
            request.getRequestDispatcher("manage_students.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            request.setAttribute("errorMessage", "Lỗi khi lưu dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("manage_students.jsp").forward(request, response);
        }
    }
}