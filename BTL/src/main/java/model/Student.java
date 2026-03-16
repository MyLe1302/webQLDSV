package model;

import java.util.Date;

public class Student {
    private String studentId;
    private String name;
    private String department;
    private int courseId;
    private Date birthDate;
    private String gender;
    private String photo;
    private int classId;
    private String className;
    private int accountId; // Giữ lại vì không thay đổi cơ sở dữ liệu

    // Constructor rỗng
    public Student() {}

    // Constructor đầy đủ
    public Student(String studentId, String name, String department, int courseId, 
                   Date birthDate, String gender, String photo, int classId, int accountId) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.courseId = courseId;
        this.birthDate = birthDate;
        this.gender = gender;
        this.photo = photo;
        this.classId = classId;
        this.accountId = accountId;
    }

    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public int getClassId() { return classId; }
    public void setClassId(int classId) { this.classId = classId; }
    
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
}