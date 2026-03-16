package servlet;

import dao.StudentDao;
import model.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@WebServlet("/AttendanceServlet")
public class AttendanceServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String classId = request.getParameter("class_id");

        // Kiểm tra dữ liệu đầu vào cơ bản
        if (classId == null || classId.trim().isEmpty() || action == null) {
            String error = URLEncoder.encode("Bạn chưa chọn buổi", "UTF-8");
            response.sendRedirect("attendance.jsp?class_id=" + classId + "&error=" + error);
            return;
        }

        StudentDao studentDao = new StudentDao();
        try {
            List<Student> students = studentDao.getStudentsByClassId(Integer.parseInt(classId));

            if ("save".equals(action)) {
                String sessionNumber = request.getParameter("session_number");
                // Kiểm tra dữ liệu khi lưu điểm danh
                if (sessionNumber == null || sessionNumber.trim().isEmpty()) {
                    String error = URLEncoder.encode("Vui lòng chọn buổi học", "UTF-8");
                    response.sendRedirect("attendance.jsp?class_id=" + classId + "&error=" + error);
                    return;
                }
                // Lưu điểm danh cho từng sinh viên
                for (Student student : students) {
                    String status = request.getParameter("status_" + student.getStudentId());
                    String note = request.getParameter("note_" + student.getStudentId());
                    if (status != null && !status.isEmpty()) {
                        studentDao.saveAttendance(Integer.parseInt(classId), student.getStudentId(), 
                                        Integer.parseInt(sessionNumber), status, note);
                    }
                }
                String success = URLEncoder.encode("Điểm danh đã được lưu thành công", "UTF-8");
                System.out.println("Chuyển hướng tới: attendance_form.jsp?class_id=" + classId + "&success=" + success);
                response.sendRedirect("attendance.jsp?class_id=" + classId + "&success=" + success);
            } else if ("showTotal".equals(action)) {
                // Hiển thị tổng số buổi nghỉ
                response.sendRedirect("attendance.jsp?class_id=" + classId + "&showTotal=true");
            } else {
                String error = URLEncoder.encode("Hành động không hợp lệ", "UTF-8");
                response.sendRedirect("attendance.jsp?class_id=" + classId + "&error=" + error);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String error = URLEncoder.encode("Lỗi: " + e.getMessage(), "UTF-8");
            response.sendRedirect("attendance.jsp?class_id=" + classId + "&error=" + error);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response); // Chuyển hướng GET sang POST
    }
}