package model;

public class Grade {
    private int gradeId;
    private String studentId;
    private String subjectId;
    private String subjectName;
    private float score;
    private float attendance;
    private float midterm;
    private float finalExam;
    private float total;
    private String letterGrade;
    private String note;

    // Constructor
    public Grade(int gradeId, String studentId, String subjectId, float score, float attendance, 
                 float midterm, float finalExam, float total, String letterGrade, String note) {
        this.gradeId = gradeId;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.score = score;
        this.attendance = attendance;
        this.midterm = midterm;
        this.finalExam = finalExam;
        this.total = total;
        this.letterGrade = letterGrade;
        this.note = note;
    }
    
    
 // Getter và Setter cho subjectName
    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    // Getters and Setters
    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public float getAttendance() {
        return attendance;
    }

    public void setAttendance(float attendance) {
        this.attendance = attendance;
    }

    public float getMidterm() {
        return midterm;
    }

    public void setMidterm(float midterm) {
        this.midterm = midterm;
    }

    public float getFinalExam() {
        return finalExam;
    }

    public void setFinalExam(float finalExam) {
        this.finalExam = finalExam;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getLetterGrade() {
        return letterGrade;
    }

    public void setLetterGrade(String letterGrade) {
        this.letterGrade = letterGrade;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}