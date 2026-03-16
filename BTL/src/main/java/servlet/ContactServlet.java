package servlet;

import dao.ContactDao;
import model.Contact;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/ContactServlet")
public class ContactServlet extends HttpServlet {
    private ContactDao contactDao;

    @Override
    public void init() throws ServletException {
        contactDao = new ContactDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Contact> contacts = contactDao.getAllContacts();
            request.setAttribute("contacts", contacts);
            request.getRequestDispatcher("contact.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Lỗi khi lấy danh sách liên hệ: " + e.getMessage());
            request.setAttribute("errorMessage", "Lỗi khi lấy danh sách liên hệ: " + e.getMessage());
            request.getRequestDispatcher("contact.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String message = request.getParameter("message");

        System.out.println("Dữ liệu nhận được - name: " + name + ", email: " + email + ", message: " + message);

        if (name == null || email == null || message == null || name.trim().isEmpty() || email.trim().isEmpty() || message.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin!");
            doGet(request, response);
            return;
        }

        Contact contact = new Contact(0, name, email, message, null);
        try {
            contactDao.addContact(contact);
            request.setAttribute("successMessage", "Gửi liên hệ thành công!");
            doGet(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Lỗi khi gửi liên hệ: " + e.getMessage());
            request.setAttribute("errorMessage", "Lỗi khi gửi liên hệ: " + e.getMessage());
            doGet(request, response);
        }
    }
}