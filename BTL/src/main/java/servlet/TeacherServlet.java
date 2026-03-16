package servlet;

import dao.TeacherDAO;
import model.Teacher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/TeacherServlet")
public class TeacherServlet extends HttpServlet {
    private TeacherDAO teacherDAO;

    @Override
    public void init() throws ServletException {
        teacherDAO = new TeacherDAO();
        if (teacherDAO == null) {
            throw new ServletException("Failed to initialize TeacherDAO");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        try {
            if (action == null || action.isEmpty()) {
                List<Teacher> teachers = teacherDAO.getAllTeachers();
                request.setAttribute("teachers", teachers);
            } else if (action.equals("delete")) {
                String teacherIdStr = request.getParameter("teacher_id");
                System.out.println("doGet: Received teacher_id for delete: [" + teacherIdStr + "]"); // Debug
                if (teacherIdStr == null || teacherIdStr.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Mã giảng viên không được để trống!");
                    System.out.println("doGet: teacher_id is null or empty"); // Debug
                } else {
                    try {
                        int teacherId = Integer.parseInt(teacherIdStr.trim());
                        if (teacherId <= 0) {
                            request.setAttribute("errorMessage", "Mã giảng viên không hợp lệ, phải là số nguyên dương!");
                            System.out.println("doGet: Invalid teacher_id (non-positive): " + teacherId); // Debug
                        } else {
                            teacherDAO.deleteTeacher(teacherId);
                            request.setAttribute("successMessage", "Xóa giảng viên thành công!");
                            System.out.println("doGet: Successfully deleted teacher ID: " + teacherId); // Debug
                        }
                    } catch (NumberFormatException e) {
                        request.setAttribute("errorMessage", "Mã giảng viên không hợp lệ, phải là số nguyên!");
                        System.out.println("doGet: NumberFormatException for teacher_id: " + teacherIdStr); // Debug
                        e.printStackTrace();
                    }
                }
                List<Teacher> teachers = teacherDAO.getAllTeachers();
                request.setAttribute("teachers", teachers);
            }
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Lỗi khi xóa giảng viên: " + e.getMessage());
            System.out.println("doGet: SQLException: " + e.getMessage()); // Debug
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            request.setAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            System.out.println("doGet: ClassNotFoundException: " + e.getMessage()); // Debug
            e.printStackTrace();
        }
        request.getRequestDispatcher("manage_teachers.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        try {
            if (action.equals("add")) {
                Teacher teacher = new Teacher();
                String name = request.getParameter("name");
                String email = request.getParameter("email");
                String phone = request.getParameter("phone");

                // Kiểm tra dữ liệu đầu vào
                if (name == null || name.trim().isEmpty()) {
                    throw new SQLException("Tên giảng viên không hợp lệ!");
                }

                teacher.setName(name.trim());
                teacher.setEmail(email != null ? email.trim() : "");
                teacher.setPhone(phone != null ? phone.trim() : "");

                teacherDAO.addTeacher(teacher);
                request.setAttribute("successMessage", "Thêm giảng viên thành công!");
                // Xóa dữ liệu form sau khi thêm thành công
                request.setAttribute("form_teacher_id", "");
                request.setAttribute("form_name", "");
                request.setAttribute("form_email", "");
                request.setAttribute("form_phone", "");
            } else if (action.equals("update")) {
                Teacher teacher = new Teacher();
                String teacherIdStr = request.getParameter("teacher_id");
                System.out.println("doPost: Received teacher_id for update: [" + teacherIdStr + "]"); // Debug
                if (teacherIdStr == null || teacherIdStr.trim().isEmpty()) {
                    throw new SQLException("Mã giảng viên không được để trống!");
                }
                try {
                    int teacherId = Integer.parseInt(teacherIdStr.trim());
                    if (teacherId <= 0) {
                        throw new SQLException("Mã giảng viên không hợp lệ, phải là số nguyên dương!");
                    }
                    teacher.setTeacherId(teacherId);
                } catch (NumberFormatException e) {
                    throw new SQLException("Mã giảng viên không hợp lệ, phải là số nguyên!");
                }
                teacher.setName(request.getParameter("name").trim());
                teacher.setEmail(request.getParameter("email") != null ? request.getParameter("email").trim() : "");
                teacher.setPhone(request.getParameter("phone") != null ? request.getParameter("phone").trim() : "");
                teacherDAO.updateTeacher(teacher);
                request.setAttribute("successMessage", "Cập nhật giảng viên thành công!");
            } else if (action.equals("delete")) {
                String teacherIdStr = request.getParameter("teacher_id");
                System.out.println("doPost: Received teacher_id for delete: [" + teacherIdStr + "]"); // Debug
                if (teacherIdStr == null || teacherIdStr.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Mã giảng viên không được để trống!");
                    System.out.println("doPost: teacher_id is null or empty"); // Debug
                } else {
                    try {
                        int teacherId = Integer.parseInt(teacherIdStr.trim());
                        if (teacherId <= 0) {
                            request.setAttribute("errorMessage", "Mã giảng viên không hợp lệ, phải là số nguyên dương!");
                            System.out.println("doPost: Invalid teacher_id (non-positive): " + teacherId); // Debug
                        } else {
                            teacherDAO.deleteTeacher(teacherId);
                            request.setAttribute("successMessage", "Xóa giảng viên thành công!");
                            System.out.println("doPost: Successfully deleted teacher ID: " + teacherId); // Debug
                        }
                    } catch (NumberFormatException e) {
                        request.setAttribute("errorMessage", "Mã giảng viên không hợp lệ, phải là số nguyên!");
                        System.out.println("doPost: NumberFormatException for teacher_id: " + teacherIdStr); // Debug
                        e.printStackTrace();
                    }
                }
            }
            List<Teacher> teachers = teacherDAO.getAllTeachers();
            request.setAttribute("teachers", teachers);
        } catch (SQLException e) {
            // Giữ dữ liệu form khi có lỗi
            request.setAttribute("errorMessage", "Lỗi khi xử lý yêu cầu: " + e.getMessage());
            request.setAttribute("form_teacher_id", request.getParameter("teacher_id"));
            request.setAttribute("form_name", request.getParameter("name"));
            request.setAttribute("form_email", request.getParameter("email"));
            request.setAttribute("form_phone", request.getParameter("phone"));
            System.out.println("doPost: SQLException: " + e.getMessage()); // Debug
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            request.setAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            System.out.println("doPost: ClassNotFoundException: " + e.getMessage()); // Debug
            e.printStackTrace();
        }
        request.getRequestDispatcher("manage_teachers.jsp").forward(request, response);
    }
}