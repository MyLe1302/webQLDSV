package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	private static final String URL = "jdbc:mysql://localhost:3060/ql_diem?useUnicode=true&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASS = "1234"; // Cập nhật với mật khẩu MySQL của bạn

    public static Connection getConnection() throws SQLException , ClassNotFoundException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Kết nối cơ sở dữ liệu thành công: " + URL); // Dòng kiểm tra kết nối
            return conn;
        } catch (ClassNotFoundException e) {
            System.out.println("Không tìm thấy driver cơ sở dữ liệu: " + e.getMessage()); // Dòng kiểm tra lỗi
            throw new SQLException("Không tìm thấy driver cơ sở dữ liệu", e);
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage()); // Dòng kiểm tra lỗi
            throw e;
        }
    }
}