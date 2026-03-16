package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Account;
import model.Grade;
import model.Student;
import model.Subject;
import dao.GradeDao;
import dao.StudentDao;
import dao.SubjectDao;

@WebServlet("/GradeServlet")
public class GradeServlet extends HttpServlet {
    private GradeDao gradeDao;
    private StudentDao studentDao;
    private SubjectDao subjectDao;

    @Override
    public void init() throws ServletException {
        try {
            gradeDao = new GradeDao();
            studentDao = new StudentDao();
            subjectDao = new SubjectDao();
            System.out.println("Khởi tạo GradeServlet thành công.");
        } catch (Exception e) {
            System.err.println("Lỗi khi khởi tạo GradeServlet: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("Không thể khởi tạo GradeServlet.", e);
        }
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

        String action = request.getParameter("action");
        if ("grades".equals(action)) {
            String studentId = request.getParameter("student_id");
            int classId = Integer.parseInt(request.getParameter("class_id"));
            try {
                List<Grade> grades = gradeDao.getGradesByClassId(classId);
                double average10 = calculateAverage10(grades);
                double average4 = convertTo4Scale(average10);
                String classification = getClassification(average10);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();

                StringBuilder json = new StringBuilder();
                json.append("{\"grades\":[");
                for (int i = 0; i < grades.size(); i++) {
                    Grade grade = grades.get(i);
                    json.append(String.format(
                        "{\"subjectId\":\"%s\",\"subjectName\":\"%s\",\"attendance\":%.1f,\"midterm\":%.1f,\"finalExam\":%.1f,\"total\":%.1f,\"letterGrade\":\"%s\",\"note\":\"%s\"}",
                        grade.getSubjectId() != null ? grade.getSubjectId() : "N/A",
                        grade.getSubjectName() != null ? grade.getSubjectName() : "N/A",
                        grade.getAttendance(),
                        grade.getMidterm(),
                        grade.getFinalExam(),
                        grade.getTotal(),
                        grade.getLetterGrade() != null ? grade.getLetterGrade() : "N/A",
                        grade.getNote() != null ? grade.getNote() : "N/A"
                    ));
                    if (i < grades.size() - 1) {
                        json.append(",");
                    }
                }
                json.append("],");
                json.append(String.format("\"average10\":%.1f,", average10));
                json.append(String.format("\"average4\":%.2f,", average4));
                json.append(String.format("\"classification\":\"%s\"}", classification));
                out.print(json.toString());
                out.flush();
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi lấy dữ liệu: " + e.getMessage());
            }
            return;
        }

        String classIdStr = request.getParameter("class_id");
        int classId = -1;
        try {
            if (classIdStr == null || classIdStr.trim().isEmpty()) {
                throw new NumberFormatException("class_id không được cung cấp hoặc rỗng.");
            }
            classId = Integer.parseInt(classIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Lớp học không hợp lệ hoặc không được cung cấp: " + e.getMessage());
            request.getRequestDispatcher("manage_grades.jsp").forward(request, response);
            return;
        }

        request.setAttribute("classId", classId);

        try {
            List<Student> students = studentDao.getStudentsByClassId(classId);
            List<Subject> subjects = subjectDao.getAllSubjects();
            request.setAttribute("students", students);
            request.setAttribute("subjects", subjects);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Lỗi khi lấy dữ liệu sinh viên/môn học: " + e.getMessage());
        }

        try {
            List<Grade> grades = gradeDao.getGradesByClassId(classId);
            request.setAttribute("grades", grades);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Lỗi khi lấy dữ liệu điểm: " + e.getMessage());
        }

        request.getRequestDispatcher("manage_grades.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        String classIdStr = request.getParameter("class_id");
        int classId;
        try {
            if (classIdStr == null || classIdStr.trim().isEmpty()) {
                throw new NumberFormatException("class_id không được cung cấp hoặc rỗng.");
            }
            classId = Integer.parseInt(classIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Lớp học không hợp lệ: " + e.getMessage());
            request.setAttribute("classId", -1);
            request.getRequestDispatcher("manage_grades.jsp").forward(request, response);
            return;
        }

        try {
            if ("add".equals(action)) {
                String studentId = request.getParameter("student_id");
                String subjectId = request.getParameter("subject_id");
                if (studentId == null || studentId.isEmpty() || subjectId == null || subjectId.isEmpty()) {
                    throw new IllegalArgumentException("Mã sinh viên hoặc môn học không được để trống.");
                }
                float score = parseFloatParameter(request.getParameter("score"), "Điểm quá trình");
                float attendance = parseFloatParameter(request.getParameter("attendance"), "Điểm chuyên cần");
                float midterm = parseFloatParameter(request.getParameter("midterm"), "Điểm giữa kỳ");
                float finalExam = parseFloatParameter(request.getParameter("final_exam"), "Điểm cuối kỳ");
                float total = (score * 0.1f) + (attendance * 0.1f) + (midterm * 0.2f) + (finalExam * 0.7f);
                String letterGrade = total >= 8.5 ? "A" : total >= 7.0 ? "B" : total >= 5.5 ? "C" : total >= 4.0 ? "D" : "F";
                String note = total >= 8.5 ? "Giỏi" : total >= 7.0 ? "Khá" : total >= 5.5 ? "Trung bình" : "Yếu";

                Grade grade = new Grade(0, studentId, subjectId, score, attendance, midterm, finalExam, total, letterGrade, note);
                gradeDao.addGrade(grade);
                request.setAttribute("successMessage", "Thêm điểm thành công!");
            } else if ("update".equals(action)) {
                int gradeId = parseIntParameter(request.getParameter("grade_id"), "Mã điểm");
                String studentId = request.getParameter("student_id");
                String subjectId = request.getParameter("subject_id");
                if (studentId == null || studentId.isEmpty() || subjectId == null || subjectId.isEmpty()) {
                    throw new IllegalArgumentException("Mã sinh viên hoặc môn học không được để trống.");
                }
                float score = parseFloatParameter(request.getParameter("score"), "Điểm quá trình");
                float attendance = parseFloatParameter(request.getParameter("attendance"), "Điểm chuyên cần");
                float midterm = parseFloatParameter(request.getParameter("midterm"), "Điểm giữa kỳ");
                float finalExam = parseFloatParameter(request.getParameter("final_exam"), "Điểm cuối kỳ");
                float total = (score * 0.1f) + (attendance * 0.1f) + (midterm * 0.2f) + (finalExam * 0.7f);
                String letterGrade = total >= 8.5 ? "A" : total >= 7.0 ? "B" : total >= 5.5 ? "C" : total >= 4.0 ? "D" : "F";
                String note = total >= 8.5 ? "Giỏi" : total >= 7.0 ? "Khá" : total >= 5.5 ? "Trung bình" : "Yếu";

                Grade grade = new Grade(gradeId, studentId, subjectId, score, attendance, midterm, finalExam, total, letterGrade, note);
                gradeDao.updateGrade(grade);
                request.setAttribute("successMessage", "Sửa điểm thành công!");
            } else if ("delete".equals(action)) {
                int gradeId = parseIntParameter(request.getParameter("grade_id"), "Mã điểm");
                gradeDao.deleteGrade(gradeId);
                request.setAttribute("successMessage", "Xóa điểm thành công!");
            } else {
                throw new IllegalArgumentException("Hành động không hợp lệ: " + action);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Lỗi: " + e.getMessage());
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Lỗi khi thực hiện hành động: " + e.getMessage());
        }

        try {
            List<Student> students = studentDao.getStudentsByClassId(classId);
            List<Subject> subjects = subjectDao.getAllSubjects();
            List<Grade> grades = gradeDao.getGradesByClassId(classId);
            request.setAttribute("students", students);
            request.setAttribute("subjects", subjects);
            request.setAttribute("grades", grades);
            request.setAttribute("classId", classId);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Lỗi khi tải lại dữ liệu: " + e.getMessage());
            request.setAttribute("classId", classId);
        }

        request.getRequestDispatcher("manage_grades.jsp").forward(request, response);
    }

    private double calculateAverage10(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) return 0.0;
        double sum = 0.0;
        for (Grade grade : grades) {
            sum += grade.getTotal();
        }
        return sum / grades.size();
    }

    private double convertTo4Scale(double average10) {
        if (average10 >= 8.5) return 4.0;
        else if (average10 >= 7.0) return 3.0;
        else if (average10 >= 5.5) return 2.0;
        else return 0.0;
    }

    private String getClassification(double average10) {
        if (average10 >= 8.5) return "Giỏi";
        else if (average10 >= 7.0) return "Khá";
        else if (average10 >= 5.5) return "Trung bình";
        else return "Yếu";
    }

    private float parseFloatParameter(String value, String fieldName) throws IllegalArgumentException {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " không được để trống.");
        }
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " phải là một số hợp lệ.");
        }
    }

    private int parseIntParameter(String value, String fieldName) throws IllegalArgumentException {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " không được để trống.");
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " phải là một số nguyên hợp lệ.");
        }
    }
}