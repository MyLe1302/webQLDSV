package model;

public class Attendance {
    private String studentId;
    private int classId;
    private int sessionNumber;
    private String status;

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public int getClassId() { return classId; }
    public void setClassId(int classId) { this.classId = classId; }

    public int getSessionNumber() { return sessionNumber; }
    public void setSessionNumber(int sessionNumber) { this.sessionNumber = sessionNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}